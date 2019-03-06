package com.springboot.autoconfig.alimns.executor;


import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.BaseMessage;
import com.springboot.autoconfig.alimns.MnsProperties;
import com.springboot.autoconfig.alimns.listener.ConsumeMessageCallback;
import com.springboot.autoconfig.alimns.listener.MnsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MnsExecutor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ThreadPoolTaskExecutor _executor;

	private ThreadPoolTaskScheduler _failedScheduler;

	private TaskExecutor _postExecutor;

	private MnsProperties mnsProperties;

	private JdbcTemplate jdbcTemplate;

	public MnsExecutor(MnsProperties mnsProperties, JdbcTemplate jdbcTemplate) {
		this.mnsProperties = mnsProperties;
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setExecutor(ThreadPoolTaskExecutor executor) {
		_executor = executor;
	}

	public void setFailedScheduler(ThreadPoolTaskScheduler failedScheduler) {
		_failedScheduler = failedScheduler;
	}

	public void setPostExecutor(TaskExecutor postExecutor) {
		_postExecutor = postExecutor;
	}

	public final void sendMessage(MessageDto messageDto) {
		beforeSend(messageDto);
		_executor.submit(() -> {
			try {
				Runnable task = MnsTaskFactory.buildSendTask(messageDto, MnsExecutor.this);
				task.run();
			} catch (Exception ex) {
				logger.error("", ex);
			}
		});
	}


	private void beforeSend(MessageDto messageDto) {

		if (needPersistMessageSendHistory()) {
			String status = "pending";

			String sql_message_send_history_insert = String.format(MessageHistorySqls.sql_message_send_history_insert, mnsProperties.getMessageSendHistoryTable());
			jdbcTemplate.update(sql_message_send_history_insert,
					new Object[]{messageDto.getId(), messageDto.getMnsRef(), messageDto.getMessageTag(), messageDto.getMessageTxt(), new Date(), status},
					new int[]{Types.CHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR}
			);
		}

	}

	public void afterSuccessSend(MessageDto messageDto) {
		logger.info("The message [{}] sent to [{}] is success. ", messageDto.getId(), messageDto.getMnsRef());

		if (needPersistMessageSendHistory()) {
			_postExecutor.execute(() -> {
				String status = "success";
				String sql_message_history_update = "update " + mnsProperties.getMessageSendHistoryTable() + " set last_send_time=?, status=? where id=?";
				jdbcTemplate.update(sql_message_history_update, new Date(), status, messageDto.getId());

			});
		}

	}

	public void afterFailedSend(MessageDto messageDto, Exception ex) {
		logger.info("The message [{}] sent to [{}] is failed. ", messageDto.getId(), messageDto.getMnsRef());
		int tries = messageDto.incrTries();
		if (needPersistMessageSendHistory()) {
			_postExecutor.execute(() -> {
				String status = "failed";
				String sql_message_send_history_update = String.format(MessageHistorySqls.sql_message_send_history_update, mnsProperties.getMessageSendHistoryTable());
				jdbcTemplate.update(sql_message_send_history_update, new Date(), status, tries, messageDto.getId());
			});
		}


		if (tries <= mnsProperties.getMessageSendMaxTries()) {
			logger.info("The message [{}] sent to [{}]  is scheduled. It re-send at [{}]", messageDto.getId(), messageDto.getMnsRef(), new Date(System.currentTimeMillis() + tries * mnsProperties.getMessageSendRetryInterval() * 1000L));
			_failedScheduler.getScheduledExecutor().schedule(() -> {
				try {
					Runnable task = MnsTaskFactory.buildSendTask(messageDto, MnsExecutor.this);
					task.run();
				} catch (Exception ex1) {
					logger.error("", ex1);
				}
			}, tries * mnsProperties.getMessageSendRetryInterval(), TimeUnit.SECONDS);
		}

	}

	private boolean needPersistMessageSendHistory() {
		return mnsProperties.getMessageSendHistoryTable() != null && jdbcTemplate != null;
	}


	public <T extends BaseMessage> void afterSuccessReceive(MessageDto messageDto, MnsListener mnsListener, T message, ConsumeMessageCallback<T> consumeMessageCallback) {
		_postExecutor.execute(() -> {
			ReceiveMessageOp op = preConsumeMessage(messageDto);
			String status = null;
			Exception ex = null;
			try {
				if (ReceiveMessageOp.do_handle == op) {
					String messageId = messageDto.getId();
					if (MessageDto.BAD_MESSAGE_ID.equals(messageId)) { //the message is bad when id is null
						logger.error("The messageListener bound on[{}] received a bad message, text:[{}]", mnsListener.getMnsRef(), messageDto.getMessageTxt());
						consumeMessageCallback.consumeOnBad(message);
						status = "bad";
					} else {
						logger.info("The messageListener bound on[{}] consume message. id:[{}], text:[{}]", mnsListener.getMnsRef(), messageDto.getId(), messageDto.getMessageTxt());
						mnsListener.onMessage(messageDto.getId(), messageDto.getMessageTxt());
						consumeMessageCallback.consumeOnSuccess(message);
						status = "success";
					}
				} else if (ReceiveMessageOp.do_delete == op) {
					consumeMessageCallback.consumeOnSuccess(message);
				}

			} catch (Exception e) {
				if (e instanceof ServiceException && "MessageNotExist".equals(((ServiceException) e).getErrorCode())) {
					status = "success";
				} else {
					consumeMessageCallback.consumeOnException(message, e);
					status = "fail";
					ex = e;
				}

			} finally {
				postConsumeMessage(messageDto, status, op, ex);
			}

		});
	}

	private ReceiveMessageOp preConsumeMessage(MessageDto messageDto) {
		logger.info("Pre consume message id:[{}], data:[{}], mnsRef:[{}]. ", messageDto.getId(), messageDto.getMessageTxt(), messageDto.getMnsRef());
		ReceiveMessageOp op = ReceiveMessageOp.no_op;
		if (mnsProperties.getMessageReceiveHistoryTable() != null && jdbcTemplate != null) {
			Date now = new Date();
			String select_receive_message_status_by_id_sql = String.format(MessageHistorySqls.sql_message_receive_history_status, mnsProperties.getMessageReceiveHistoryTable());
			List<ReceivedMessageStatusObject> receivedMessageStatusObjects = jdbcTemplate.query(select_receive_message_status_by_id_sql, new Object[]{messageDto.getId()},
					(rs, rowNum) -> new ReceivedMessageStatusObject(
							rs.getString("id"),
							rs.getString("status"),
							rs.getTimestamp("consume_time"),
							rs.getInt("consume_tries")));
			ReceivedMessageStatusObject receivedMessageStatusObject = receivedMessageStatusObjects.isEmpty() ? null : receivedMessageStatusObjects.get(0);

			if (receivedMessageStatusObject == null) {
				String sql_receive_message_history_insert = String.format(MessageHistorySqls.sql_message_receive_history_insert, mnsProperties.getMessageReceiveHistoryTable());
				jdbcTemplate.update(sql_receive_message_history_insert, messageDto.getId(), messageDto.getMessageTxt(), now, now, 1, "pending", messageDto.getMnsRef());
				op = ReceiveMessageOp.do_handle;
			} else if ("fail".equals(receivedMessageStatusObject.getStatus())) {
				if (mnsProperties.getMessageConsumeMaxTries() != 0 && receivedMessageStatusObject.getConsumeTries() >= mnsProperties.getMessageConsumeMaxTries()) {
					op = ReceiveMessageOp.do_delete;
				} else {
					op = ReceiveMessageOp.do_handle;
					String sql_receive_message_history_update = String.format(MessageHistorySqls.sql_message_receive_history_update, mnsProperties.getMessageReceiveHistoryTable());
					jdbcTemplate.update(sql_receive_message_history_update, now, receivedMessageStatusObject.getConsumeTries() + 1, "pending", receivedMessageStatusObject.getId(), receivedMessageStatusObject.getConsumeTime(), receivedMessageStatusObject.getStatus());

				}
			} else if (("pending".equals(receivedMessageStatusObject.getStatus()) && (now.getTime() - receivedMessageStatusObject.getConsumeTime().getTime()) > mnsProperties.getMessageReceiveHandleMaxInterval() * 1000)) {
				String sql_receive_message_history_update = String.format(MessageHistorySqls.sql_message_receive_history_update, mnsProperties.getMessageReceiveHistoryTable());
				int rowCount = jdbcTemplate.update(sql_receive_message_history_update, now, receivedMessageStatusObject.getConsumeTries() + 1, "pending", receivedMessageStatusObject.getId(), receivedMessageStatusObject.getConsumeTime(), receivedMessageStatusObject.getStatus());
				if (rowCount == 1) {
					op = ReceiveMessageOp.do_handle;
				}
			} else if ("success".equals(receivedMessageStatusObject.getStatus())) {
				op = ReceiveMessageOp.do_delete;
			}
		} else {
			op = ReceiveMessageOp.do_handle;
		}
		return op;

	}

	private void postConsumeMessage(MessageDto messageDto, String status, ReceiveMessageOp op, Exception ex) {
		logger.info("Post consume message id:[{}], status:[{}], mnsRef:[{}], op:[{}] error message:[{}]", messageDto.getId(), status, messageDto.getMnsRef(), op, ex != null ? ex.getMessage() : null);
		if (mnsProperties.getMessageReceiveHistoryTable() != null && jdbcTemplate != null && ReceiveMessageOp.do_handle == op) {
			String sql_receive_message_history_update_status = String.format(MessageHistorySqls.sql_message_receive_history_update_status, mnsProperties.getMessageReceiveHistoryTable());
			jdbcTemplate.update(sql_receive_message_history_update_status, status, messageDto.getId());
		}

	}


	private enum ReceiveMessageOp {
		no_op, do_handle, do_delete
	}


	private static class ReceivedMessageStatusObject {
		private final String id;
		private final String status;
		private final Date consumeTime;
		private final int consumeTries;

		ReceivedMessageStatusObject(String id, String status, Date consumeTime, int consumeTries) {
			this.id = id;
			this.status = status;
			this.consumeTime = consumeTime;
			this.consumeTries = consumeTries;
		}

		String getId() {
			return id;
		}

		String getStatus() {
			return status;
		}

		Date getConsumeTime() {
			return consumeTime;
		}

		int getConsumeTries() {
			return consumeTries;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ReceivedMessageStatusObject that = (ReceivedMessageStatusObject) o;
			return Objects.equals(id, that.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}
	}


}

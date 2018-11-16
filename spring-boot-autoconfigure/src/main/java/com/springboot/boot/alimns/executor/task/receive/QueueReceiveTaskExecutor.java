package com.springboot.boot.alimns.executor.task.receive;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.springboot.boot.alimns.executor.MnsClientFactory;
import com.springboot.boot.alimns.executor.MnsExecutor;
import com.springboot.boot.alimns.listener.ConsumeMessageCallback;
import com.springboot.boot.alimns.listener.MnsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueReceiveTaskExecutor extends AbstractReceiveTaskExecutor<Message> {

	private final static Logger logger = LoggerFactory.getLogger(QueueReceiveTaskExecutor.class);

	private final String _queueName;
	private final ExecutorService _executor = Executors.newSingleThreadExecutor();

	private volatile boolean _stop = false;

	public QueueReceiveTaskExecutor(MnsListener mnsListener, MnsExecutor mnsExecutor, String queueName) {
		super(mnsListener, mnsExecutor);
		this._queueName = queueName;
	}


	@Override
	public void run() {
		CloudQueue queue = MnsClientFactory.getClient().getQueueRef(_queueName);
		_executor.execute(() -> {
			while (!_stop) {
				try {
					List<Message> messages = queue.batchPopMessage(10, 30);
					if (messages != null && !messages.isEmpty()) {
						logger.info("The queue[{}] received [{}] messages. ", _queueName, messages.size());
						QueueConsumeCallback queueConsumeCallback = new QueueConsumeCallback(queue);
						for (Message message : messages) {
							consumeMessage(message, queueConsumeCallback);
						}
					}
				} catch (Exception ex) {
					logger.error("", ex);
				}
			}
		});
	}

	public void stop() {
		_stop = true;
		_executor.shutdownNow();
	}


	private static class QueueConsumeCallback implements ConsumeMessageCallback<Message> {

		private final CloudQueue _queue;

		private QueueConsumeCallback(CloudQueue queue) {
			_queue = queue;
		}

		@Override
		public String getMessageBody(Message message) {
			return message.getMessageBodyAsRawString();
		}

		@Override
		public void consumeOnSuccess(Message message) {
			_queue.deleteMessage(message.getReceiptHandle());
		}

		@Override
		public void consumeOnException(Message message, Exception ex) {
			logger.error("The messageListener consume message error, it retry it later. dequeueCount:[{}], text:[{}]", message.getDequeueCount(), message.getMessageBodyAsRawString(), ex);
		}

		@Override
		public void consumeOnBad(Message message) {
			_queue.deleteMessage(message.getReceiptHandle());
		}

	}


}

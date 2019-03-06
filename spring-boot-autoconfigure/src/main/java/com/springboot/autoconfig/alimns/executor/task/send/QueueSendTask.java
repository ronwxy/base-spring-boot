package com.springboot.autoconfig.alimns.executor.task.send;


import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.springboot.autoconfig.alimns.executor.MessageDto;
import com.springboot.autoconfig.alimns.executor.MnsClientFactory;
import com.springboot.autoconfig.alimns.executor.MnsExecutor;
import org.apache.commons.collections4.MapUtils;

public class QueueSendTask extends AbstractSendTask<Message> {

	private final String _queueName;

	public QueueSendTask(MessageDto messageDto, MnsExecutor mnsExecutor, String queueName) {
		super(messageDto, mnsExecutor);
		this._queueName = queueName;
	}


	public void run() {
		Message message = createQueueMessage();
		CloudQueue queue = MnsClientFactory.getClient().getQueueRef(_queueName);
		queue.asyncPutMessage(message, new DefaultAsyncCallback<>());
	}


	private Message createQueueMessage() {
		Message message = new Message();
		message.setMessageBody(createMnsTxt(), Message.MessageBodyType.RAW_STRING);

		Integer delaySeconds = MapUtils.getInteger(_messageDto.getMessageAttributes(), "delaySeconds");
		if (delaySeconds != null) {
			message.setDelaySeconds(delaySeconds);
		}

		Integer priority = MapUtils.getInteger(_messageDto.getMessageAttributes(), "priority");
		if (priority != null) {
			message.setPriority(priority);
		}
		return message;
	}


}

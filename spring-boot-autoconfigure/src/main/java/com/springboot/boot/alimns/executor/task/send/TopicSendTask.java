package com.springboot.boot.alimns.executor.task.send;


import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.model.MailAttributes;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;
import com.springboot.boot.alimns.executor.MessageDto;
import com.springboot.boot.alimns.executor.MnsClientFactory;
import com.springboot.boot.alimns.executor.MnsExecutor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class TopicSendTask extends AbstractSendTask<TopicMessage> {

	private final String _topicName;

	public TopicSendTask(MessageDto messageDto, MnsExecutor mnsExecutor, String topicName) {
		super(messageDto, mnsExecutor);
		_topicName = topicName;
	}

	public void run() {
		RawTopicMessage topicMessage = createTopicMessage();
		CloudTopic topic = MnsClientFactory.getClient().getTopicRef(_topicName);
		topic.asyncPublishMessage(topicMessage, new DefaultAsyncCallback<TopicMessage>());
	}

	private RawTopicMessage createTopicMessage() {
		RawTopicMessage message = new RawTopicMessage();
		message.setMessageTag(_messageDto.getMessageTag());
		message.setMessageBody(createMnsTxt());
		return message;
	}

	private MessageAttributes createMessageAttribute() {

		boolean isSet = false;
		Map<String, Object> attributes = _messageDto.getMessageAttributes();
		if (MapUtils.isNotEmpty(attributes)) {
			MailAttributes mailAttributes = new MailAttributes();

			String accountName = MapUtils.getString(attributes, "accountName");
			if (StringUtils.isNotEmpty(accountName)) {
				mailAttributes.setAccountName(accountName);
				isSet = true;
			}
			Integer addressType = MapUtils.getInteger(attributes, "addressType");
			if (addressType != null) {
				mailAttributes.setAddressType(addressType);
				isSet = true;
			}

			Boolean html = MapUtils.getBoolean(attributes, "html");
			if (html != null) {
				mailAttributes.setHtml(html);
				isSet = true;
			}

			Boolean replyToAddress = MapUtils.getBoolean(attributes, "replyToAddress");
			if (replyToAddress != null) {
				mailAttributes.setReplyToAddress(replyToAddress);
				isSet = true;
			}

			String subject = MapUtils.getString(attributes, "subject");
			if (StringUtils.isNotEmpty(subject)) {
				mailAttributes.setSubject(subject);
				isSet = true;
			}

			if (isSet) {
				MessageAttributes messageAttributes = new MessageAttributes();
				messageAttributes.setMailAttributes(mailAttributes);
				return messageAttributes;
			}

		}
		return null;
	}


}

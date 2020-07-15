package cn.jboost.springboot.autoconfig.alimns.executor.task.send;


import cn.hutool.core.map.MapUtil;
import cn.jboost.springboot.autoconfig.alimns.executor.MessageDto;
import cn.jboost.springboot.autoconfig.alimns.executor.MnsClientFactory;
import cn.jboost.springboot.autoconfig.alimns.executor.MnsExecutor;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.model.MailAttributes;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class TopicSendTask extends AbstractSendTask {

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
		message.setMessageTag(messageDto.getMessageTag());
		message.setMessageBody(createMnsTxt());
		return message;
	}

	private MessageAttributes createMessageAttribute() {

		boolean isSet = false;
		Map<String, Object> attributes = messageDto.getMessageAttributes();
		if (MapUtil.isNotEmpty(attributes)) {
			MailAttributes mailAttributes = new MailAttributes();

			String accountName = MapUtil.getStr(attributes, "accountName");
			if (StringUtils.isNotEmpty(accountName)) {
				mailAttributes.setAccountName(accountName);
				isSet = true;
			}
			Integer addressType = MapUtil.getInt(attributes, "addressType");
			if (addressType != null) {
				mailAttributes.setAddressType(addressType);
				isSet = true;
			}

			Boolean html = MapUtil.getBool(attributes, "html");
			if (html != null) {
				mailAttributes.setHtml(html);
				isSet = true;
			}

			Boolean replyToAddress = MapUtil.getBool(attributes, "replyToAddress");
			if (replyToAddress != null) {
				mailAttributes.setReplyToAddress(replyToAddress);
				isSet = true;
			}

			String subject = MapUtil.getStr(attributes, "subject");
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

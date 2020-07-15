package cn.jboost.springboot.autoconfig.alimns.executor.task.receive;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.jboost.springboot.autoconfig.alimns.executor.MessageDto;
import cn.jboost.springboot.autoconfig.alimns.executor.MnsExecutor;
import cn.jboost.springboot.autoconfig.alimns.listener.ConsumeMessageCallback;
import cn.jboost.springboot.autoconfig.alimns.listener.MnsListener;
import com.aliyun.mns.model.BaseMessage;


import java.util.Map;

public abstract class AbstractReceiveTaskExecutor<T extends BaseMessage> implements ReceiveTaskExecutor {

	protected final MnsListener mnsListener;

	protected final MnsExecutor mnsExecutor;

	protected AbstractReceiveTaskExecutor(MnsListener mnsListener, MnsExecutor mnsExecutor) {
		this.mnsListener = mnsListener;
		this.mnsExecutor = mnsExecutor;
	}

	protected void consumeMessage(T message, ConsumeMessageCallback<T> consumeMessageCallback) {
		String messageBody = consumeMessageCallback.getMessageBody(message);
		MessageDto messageDto = createReceivedMessage(messageBody);
		mnsExecutor.afterSuccessReceive(messageDto, mnsListener, message, consumeMessageCallback);

	}

	private MessageDto createReceivedMessage(String messageBody) {
		MessageDto messageDto;
		try {
			Map<String, Object> map = JSONUtil.toBean(messageBody, Map.class);
			String id = MapUtil.getStr(map, "_id");
			String messageTxt = MapUtil.getStr(map, "content");
			messageDto = new MessageDto(id, mnsListener.getMnsRef(), messageTxt);
			return messageDto;
		} catch (Exception ex) {
			messageDto = new MessageDto(MessageDto.BAD_MESSAGE_ID, mnsListener.getMnsRef(), messageBody);
		}
		return messageDto;

	}


}

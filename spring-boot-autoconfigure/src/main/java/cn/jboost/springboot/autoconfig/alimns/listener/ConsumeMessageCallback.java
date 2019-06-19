package cn.jboost.springboot.autoconfig.alimns.listener;

import com.aliyun.mns.model.BaseMessage;

public interface ConsumeMessageCallback<T extends BaseMessage> {

	String getMessageBody(T message);

	void consumeOnSuccess(T message);

	void consumeOnException(T message, Exception ex);

	void consumeOnBad(T message);
}

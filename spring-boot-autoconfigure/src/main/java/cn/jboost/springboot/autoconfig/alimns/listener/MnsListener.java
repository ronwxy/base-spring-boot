package cn.jboost.springboot.autoconfig.alimns.listener;

public interface MnsListener {

	String getMnsRef();

	void onMessage(String messageId, String messageText) throws Exception;

}

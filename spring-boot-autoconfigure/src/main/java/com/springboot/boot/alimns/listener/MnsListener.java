package com.springboot.boot.alimns.listener;

public interface MnsListener {

	String getMnsRef();

	void onMessage(String messageId, String messageText) throws Exception;

}

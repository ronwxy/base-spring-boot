package com.springboot.boot.alimns;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "mns")
public class MnsProperties implements InitializingBean {
	private String accessId;
	private String accessKey;
	private String accountEndpoint;
	private String messageSendHistoryTable;
	private String messageReceiveHistoryTable;
	private short messageSendMaxTries = 3;
	private long messageSendRetryInterval = 30;
	private long messageReceiveHandleMaxInterval = 120;
	private short messageConsumeMaxTries = 0;

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getAccountEndpoint() {
		return accountEndpoint;
	}

	public void setAccountEndpoint(String accountEndpoint) {
		this.accountEndpoint = accountEndpoint;
	}

	public String getMessageSendHistoryTable() {
		return messageSendHistoryTable;
	}

	public void setMessageSendHistoryTable(String messageSendHistoryTable) {
		this.messageSendHistoryTable = messageSendHistoryTable;
	}

	public String getMessageReceiveHistoryTable() {
		return messageReceiveHistoryTable;
	}

	public void setMessageReceiveHistoryTable(String messageReceiveHistoryTable) {
		this.messageReceiveHistoryTable = messageReceiveHistoryTable;
	}

	public short getMessageSendMaxTries() {
		return messageSendMaxTries;
	}

	public void setMessageSendMaxTries(short messageSendMaxTries) {
		this.messageSendMaxTries = messageSendMaxTries;
	}

	public long getMessageSendRetryInterval() {
		return messageSendRetryInterval;
	}

	public void setMessageSendRetryInterval(long messageSendRetryInterval) {
		this.messageSendRetryInterval = messageSendRetryInterval;
	}

	public long getMessageReceiveHandleMaxInterval() {
		return messageReceiveHandleMaxInterval;
	}

	public void setMessageReceiveHandleMaxInterval(long messageReceiveHandleMaxInterval) {
		this.messageReceiveHandleMaxInterval = messageReceiveHandleMaxInterval;
	}

	public short getMessageConsumeMaxTries() {
		return messageConsumeMaxTries;
	}

	public void setMessageConsumeMaxTries(short messageConsumeMaxTries) {
		this.messageConsumeMaxTries = messageConsumeMaxTries;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this, String.format("mnsProperties's properties must be set,[accessId=%s,accessKey=%s,accountEndpoint=%s,messageSendHistoryTable=%s,messageReceiveHistoryTable=%s],please check you configuration file", getAccessId(), getAccessKey(), getAccountEndpoint(), getMessageSendHistoryTable(), getMessageReceiveHistoryTable()));
	}
}

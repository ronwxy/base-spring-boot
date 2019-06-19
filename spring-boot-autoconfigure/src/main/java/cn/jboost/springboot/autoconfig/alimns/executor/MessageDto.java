package cn.jboost.springboot.autoconfig.alimns.executor;


import java.util.HashMap;
import java.util.Map;

public class MessageDto {

	public static final String BAD_MESSAGE_ID = "#!0x";

	private final String _id;

	private final String _messageTxt;

	private final String _messageTag;

	private final String _mnsRef;

	private final Map<String, Object> _messageAttributes;

	private short _tries = 0;


	public MessageDto(String id, String mnsRef, String messageTxt) {
		this(id, mnsRef, messageTxt, null);
	}

	public MessageDto(String id, String mnsRef, String messageTxt, String messageTag) {
		this(id, mnsRef, messageTxt, messageTag, new HashMap<String, Object>());
	}

	public MessageDto(String id, String mnsRef, String messageTxt, String messageTag, Map<String, Object> messageAttributes) {
		this._id = id;
		this._mnsRef = mnsRef;
		this._messageTxt = messageTxt;
		this._messageTag = messageTag;
		this._messageAttributes = messageAttributes;
	}

	public String getId() {
		return _id;
	}

	public String getMnsRef() {
		return _mnsRef;
	}

	public String getMessageTxt() {
		return _messageTxt;
	}

	public String getMessageTag() {
		return _messageTag;
	}

	public Map<String, Object> getMessageAttributes() {
		return _messageAttributes;
	}

	public short incrTries() {
		_tries++;
		return _tries;
	}

	public int getTries() {
		return _tries;
	}


}

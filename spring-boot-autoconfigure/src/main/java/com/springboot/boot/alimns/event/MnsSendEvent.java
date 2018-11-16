package com.springboot.boot.alimns.event;

import org.springframework.context.ApplicationEvent;

import java.util.Collection;
import java.util.HashSet;

public class MnsSendEvent extends ApplicationEvent {

	private static final long serialVersionUID = 7085549747123375341L;
	public final String _messageTxt;
	private final Collection<String> _mnsRefs;
	private final String _messageTag;

	public MnsSendEvent(Object source, String mnsRef, String messageTxt) {
		this(source, mnsRef, messageTxt, null);
	}

	public MnsSendEvent(Object source, String mnsRef, String messageTxt, String messageTag) {
		super(source);
		this._mnsRefs = new HashSet<>();
		this._mnsRefs.add(mnsRef);
		this._messageTxt = messageTxt;
		this._messageTag = messageTag;
	}

	public MnsSendEvent(Object source, Collection<String> mnsRefs, String messageTxt) {
		this(source, mnsRefs, messageTxt, null);
	}

	public MnsSendEvent(Object source, Collection<String> mnsRefs, String messageTxt, String messageTag) {
		super(source);
		this._mnsRefs = mnsRefs;
		this._messageTxt = messageTxt;
		this._messageTag = messageTag;
	}

	public Collection<String> getMnsRefs() {
		return _mnsRefs;
	}

	public String getMessageTag() {
		return _messageTag;
	}

	public String getMessageTxt() {
		return _messageTxt;
	}

}

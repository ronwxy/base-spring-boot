package com.springboot.autoconfig.alimns.eventhandler;


import com.springboot.autoconfig.alimns.event.MnsSendEvent;
import com.springboot.autoconfig.alimns.executor.MessageDto;
import com.springboot.autoconfig.alimns.executor.MnsExecutor;
import com.springboot.autoconfig.alimns.utils.ObjectId;
import org.springframework.context.ApplicationListener;

import java.util.Collection;

public class MnsSendEventHandler implements ApplicationListener<MnsSendEvent> {

	private MnsExecutor _mnsExecutor;

	public void setMnsExecutor(MnsExecutor mnsExecutor) {
		this._mnsExecutor = mnsExecutor;
	}

	public void onApplicationEvent(MnsSendEvent event) {
		Collection<String> mnsRefs = event.getMnsRefs();
		if (mnsRefs != null && !mnsRefs.isEmpty()) {
			for (String mnsRef : mnsRefs) {
				String id = ObjectId.get().toHexString();
				MessageDto messageDto = new MessageDto(id, mnsRef, event.getMessageTxt(), event.getMessageTag());
				_mnsExecutor.sendMessage(messageDto);
			}
		}

	}
}

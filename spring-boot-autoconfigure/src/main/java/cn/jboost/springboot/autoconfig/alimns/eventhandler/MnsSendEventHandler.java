package cn.jboost.springboot.autoconfig.alimns.eventhandler;


import cn.jboost.springboot.autoconfig.alimns.event.MnsSendEvent;
import cn.jboost.springboot.autoconfig.alimns.executor.MessageDto;
import cn.jboost.springboot.autoconfig.alimns.executor.MnsExecutor;
import cn.jboost.springboot.common.uuid.ObjectId;
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

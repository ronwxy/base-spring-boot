package com.springboot.boot.alimns.executor.task.send;

import com.aliyun.mns.client.AsyncCallback;
import com.aliyun.mns.model.BaseMessage;
import com.springboot.boot.alimns.executor.MessageDto;
import com.springboot.boot.alimns.executor.MnsExecutor;
import com.springboot.boot.alimns.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSendTask<T extends BaseMessage> implements Runnable {

	protected final MessageDto _messageDto;
	protected final MnsExecutor _mnsExecutor;

	public AbstractSendTask(MessageDto messageDto, MnsExecutor mnsExecutor) {
		this._messageDto = messageDto;
		this._mnsExecutor = mnsExecutor;
	}

	protected String createMnsTxt() {
		Map<String, Object> messageMap = new HashMap<>();
		messageMap.put("_id", _messageDto.getId());
		messageMap.put("content", _messageDto.getMessageTxt());
		return JsonUtil.toJson(messageMap);
	}

	protected class DefaultAsyncCallback<T extends BaseMessage> implements AsyncCallback<T> {
		@Override
		public void onSuccess(T result) {
			_mnsExecutor.afterSuccessSend(_messageDto);
		}

		@Override
		public void onFail(Exception ex) {
			_mnsExecutor.afterFailedSend(_messageDto, ex);
		}

	}

}

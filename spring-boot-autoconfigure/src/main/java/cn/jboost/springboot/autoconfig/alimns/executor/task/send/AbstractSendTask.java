package cn.jboost.springboot.autoconfig.alimns.executor.task.send;

import cn.jboost.springboot.autoconfig.alimns.executor.MessageDto;
import cn.jboost.springboot.autoconfig.alimns.executor.MnsExecutor;
import cn.jboost.springboot.common.jackson.JsonUtil;
import com.aliyun.mns.client.AsyncCallback;
import com.aliyun.mns.model.BaseMessage;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSendTask implements Runnable {

	protected final MessageDto messageDto;
	protected final MnsExecutor mnsExecutor;

	public AbstractSendTask(MessageDto messageDto, MnsExecutor mnsExecutor) {
		this.messageDto = messageDto;
		this.mnsExecutor = mnsExecutor;
	}

	protected String createMnsTxt() {
		Map<String, Object> messageMap = new HashMap<>();
		messageMap.put("_id", messageDto.getId());
		messageMap.put("content", messageDto.getMessageTxt());
		return JsonUtil.toJson(messageMap);
	}

	protected class DefaultAsyncCallback<T extends BaseMessage> implements AsyncCallback<T> {
		@Override
		public void onSuccess(T result) {
			mnsExecutor.afterSuccessSend(messageDto);
		}

		@Override
		public void onFail(Exception ex) {
			mnsExecutor.afterFailedSend(messageDto, ex);
		}

	}

}

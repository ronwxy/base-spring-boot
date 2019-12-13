package cn.jboost.springboot.autoconfig.alimq.producer;

import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ray4work@126.com
 * @date 2017/10/30 8:43
 */
@Slf4j
public class DefaultSendCallback implements SendCallback {


    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("send message success. {}", sendResult.toString());
    }

    @Override
    public void onException(OnExceptionContext context) {
        log.warn("send message failed. topic=" + context.getTopic() + ", msgId=" + context.getMessageId(), context.getException());
    }
}

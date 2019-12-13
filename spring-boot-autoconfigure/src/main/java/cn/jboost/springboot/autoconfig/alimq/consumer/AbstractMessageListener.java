package cn.jboost.springboot.autoconfig.alimq.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息监听者需要继承该抽象类，实现handle方法，消息消费逻辑处理
 * 如果抛出异常，则重新入队列
 *
 * @author ray4work@126.com
 * @date 2017/10/30 8:58
 */
@Slf4j
public abstract class AbstractMessageListener implements MessageListener {

    public abstract void handle(String body);

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String messageBody = new String(message.getBody());
        log.info("receive message. [topic: {}, tag: {}, body: {}, msgId: {}, startDeliverTime: {}]", message.getTopic(),
                message.getTag(), messageBody, message.getMsgID(), message.getStartDeliverTime());
        try {
            handle(new String(message.getBody()));
            log.info("handle message success.");
            return Action.CommitMessage;
        } catch (Exception e) {
            //消费失败
            log.warn("handle message fail, requeue it.", e);
            return Action.ReconsumeLater;
        }
    }
}

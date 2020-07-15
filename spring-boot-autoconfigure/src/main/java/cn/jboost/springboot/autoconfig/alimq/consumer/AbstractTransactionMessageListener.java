package cn.jboost.springboot.autoconfig.alimq.consumer;

import cn.hutool.json.JSONUtil;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public abstract class AbstractTransactionMessageListener implements MessageListener {

    public abstract void handle(String msgId, Map<String, Object> msgMap);

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String messageBody = new String(message.getBody());
        log.info("receive message. [topic: {}, tag: {}, body: {}, msgId: {}, startDeliverTime: {}]", message.getTopic(), message.getTag(), messageBody, message.getMsgID(), message.getStartDeliverTime());
        try {
            Map<String, Object> msgMap = (Map<String, Object>) JSONUtil.toBean(messageBody, Map.class);
            handle(message.getMsgID(), msgMap);
            log.info("handle message success.");
            return Action.CommitMessage;
        } catch (Exception e) {
            //消费失败
            log.warn("handle message fail, requeue it.", e);
            return Action.ReconsumeLater;
        }
    }
}

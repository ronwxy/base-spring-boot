package cn.jboost.springboot.autoconfig.alimq.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import cn.jboost.springboot.common.jackson.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractTransactionMessageListener implements MessageListener {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractTransactionMessageListener.class);

    public abstract void handle(String msgId, Map<String, Object> msgMap);

    @Override
    public Action consume(Message message, ConsumeContext context) {
        LOG.info("receive message. [topic: {}, tag: {}, body: {}, msgId: {}, startDeliverTime: {}]", message.getTopic(), message.getTag(), new String(message.getBody()), message.getMsgID(), message.getStartDeliverTime());
        try {
            Map<String, Object> msgMap = (Map<String, Object>) JsonUtil.fromJson(message.getBody(), Map.class);
            handle(message.getMsgID(), msgMap);
            LOG.info("handle message success.");
            return Action.CommitMessage;
        } catch (Exception e) {
            //消费失败
            LOG.warn("handle message fail, requeue it.", e);
            return Action.ReconsumeLater;
        }
    }
}

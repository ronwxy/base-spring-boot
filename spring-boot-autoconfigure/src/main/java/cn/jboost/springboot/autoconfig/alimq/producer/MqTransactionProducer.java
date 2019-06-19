package cn.jboost.springboot.autoconfig.alimq.producer;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 事务消息队列生产者
 */
public class MqTransactionProducer {

    private final static Logger LOG = LoggerFactory.getLogger(MqTransactionProducer.class);
    private Properties properties;
    private TransactionProducer tranProducer;
    private String tranTopic;

    public MqTransactionProducer(Properties properties) {
        if (properties == null || properties.get(PropertyKeyConst.ProducerId) == null
                || properties.get(PropertyKeyConst.AccessKey) == null
                || properties.get(PropertyKeyConst.SecretKey) == null
                || properties.get(PropertyKeyConst.ONSAddr) == null
                || properties.get("tranTopic") == null) {
            throw new ONSClientException("tran producer properties not set properly.");
        }
        this.properties = properties;
        this.tranTopic = properties.getProperty("tranTopic");
    }

    public void start(LocalTransactionChecker localTransactionChecker) {
        this.tranProducer = ONSFactory.createTransactionProducer(this.properties, localTransactionChecker);
        this.tranProducer.start();
    }

    public void shutdown() {
        if (this.tranProducer != null) {
            this.tranProducer.shutdown();
        }
    }

    public void send(String tag, String body, LocalTransactionExecuter localTransactionExecuter, Object arg) {
        LOG.info("start to send transaction message. [topic: {}, tag: {}, body: {}]", tranTopic, tag, body);
        if (tranTopic == null || tag == null || body == null) {
            throw new RuntimeException("topic, tag, or body is null.");
        }
        Message message = new Message(tranTopic, tag, body.getBytes());
        try {
            SendResult result = this.tranProducer.send(message, localTransactionExecuter, arg);
            LOG.info("send transaction message success. ", result.toString());
        }catch (Exception ex){
            LOG.error("send transaction message failed. Topic is: {}", tranTopic, ex);
            try {
                Thread.sleep(1000);
                LOG.info("retry send transaction message...");
                SendResult result = this.tranProducer.send(message, localTransactionExecuter, null);
                LOG.info("retry send transaction message success. ", result.toString());
            }catch (Exception ex2){
                LOG.error("retry send transaction message failed. Topic is: {}", tranTopic, ex2);
                throw new RuntimeException(ex2);
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean isStarted() {
        return this.tranProducer.isStarted();
    }

    public boolean isClosed() {
        return this.tranProducer.isClosed();
    }
}

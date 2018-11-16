package com.springboot.boot.alimq.consumer;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class MqTransactionConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(MqConsumer.class);
    private Properties properties;
    private Consumer tranConsumer;
    private String tranTopic;

    public MqTransactionConsumer(Properties properties) {
        if (properties == null || properties.get(PropertyKeyConst.ConsumerId) == null
                || properties.get(PropertyKeyConst.AccessKey) == null
                || properties.get(PropertyKeyConst.SecretKey) == null
                || properties.get(PropertyKeyConst.ONSAddr) == null
                || properties.get("tranTopic") == null) {
            throw new ONSClientException("consumer properties not set properly.");
        }
        this.properties = properties;
        this.tranTopic = properties.getProperty("tranTopic");
    }

    public void start() {
        this.tranConsumer = ONSFactory.createConsumer(properties);
        this.tranConsumer.start();
    }

    public void shutdown() {
        if (this.tranConsumer != null) {
            this.tranConsumer.shutdown();
        }
    }

    /**
     * @param tags            多个tag用'||'拼接，所有用*
     * @param messageListener
     */
    public void subscribe(String tags, MessageListener messageListener) {
        LOG.info("subscribe [topic: {}, tags: {}, messageListener: {}]", tranTopic, tags, messageListener.getClass().getCanonicalName());
        tranConsumer.subscribe(tranTopic, tags, messageListener);
    }
}

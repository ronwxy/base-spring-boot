package cn.jboost.springboot.autoconfig.alimq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * @author ray4work@126.com
 * @date 2017/10/27 14:06
 */
@ConfigurationProperties(prefix = "aliyun.mq")
public class MqPropertiesConfig {
    private String onsAddr;
    private String accessKey;
    private String secretKey;
    private String topic;
    private Properties producer;
    private Properties consumer;
    private String tagSuffix;
    //分布式事务消息队列配置属性
    private String tranTopic;
    private Properties tranProducer;
    private Properties tranConsumer;
    private String tranTagSuffix;

    public String getOnsAddr() {
        return onsAddr;
    }

    public void setOnsAddr(String onsAddr) {
        this.onsAddr = onsAddr;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Properties getProducer() {
        return producer;
    }

    public void setProducer(Properties producer) {
        this.producer = producer;
    }

    public Properties getConsumer() {
        return consumer;
    }

    public void setConsumer(Properties consumer) {
        this.consumer = consumer;
    }

    public String getTagSuffix() {
        return tagSuffix;
    }

    public void setTagSuffix(String tagSuffix) {
        this.tagSuffix = tagSuffix;
    }

    public String getTranTopic() {
        return tranTopic;
    }

    public void setTranTopic(String tranTopic) {
        this.tranTopic = tranTopic;
    }

    public Properties getTranProducer() {
        return tranProducer;
    }

    public void setTranProducer(Properties tranProducer) {
        this.tranProducer = tranProducer;
    }

    public Properties getTranConsumer() {
        return tranConsumer;
    }

    public void setTranConsumer(Properties tranConsumer) {
        this.tranConsumer = tranConsumer;
    }

    public String getTranTagSuffix() {
        return tranTagSuffix;
    }

    public void setTranTagSuffix(String tranTagSuffix) {
        this.tranTagSuffix = tranTagSuffix;
    }
}

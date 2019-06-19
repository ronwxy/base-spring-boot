package cn.jboost.springboot.autoconfig.alimq.config;

import cn.jboost.springboot.autoconfig.alimq.consumer.MqConsumer;
import cn.jboost.springboot.autoconfig.alimq.consumer.MqTransactionConsumer;
import cn.jboost.springboot.autoconfig.alimq.producer.MqTimerProducer;
import cn.jboost.springboot.autoconfig.alimq.producer.MqTransactionProducer;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author ray4work@126.com
 * @date 2017/10/27 14:23
 */
@Configuration
@ConditionalOnClass(Producer.class)
@EnableConfigurationProperties(MqPropertiesConfig.class)
public class AliMQAutoConfiguration {

    @Autowired
    private MqPropertiesConfig propConfig;

    private LocalTransactionChecker localTransactionChecker;

    @Autowired(required = false)
    public void setLocalTransactionChecker(LocalTransactionChecker localTransactionChecker){
        this.localTransactionChecker = localTransactionChecker;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "enabled", havingValue = "true")
    public MqTimerProducer mqTimerProducer() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ProducerId, propConfig.getProducer().getProperty("producerId"));
        properties.setProperty(PropertyKeyConst.AccessKey, propConfig.getAccessKey());
        properties.setProperty(PropertyKeyConst.SecretKey, propConfig.getSecretKey());
        properties.setProperty(PropertyKeyConst.ONSAddr, propConfig.getOnsAddr());
        properties.setProperty("topic", propConfig.getTopic());
        return new MqTimerProducer(properties);
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.consumer", value = "enabled", havingValue = "true")
    public MqConsumer mqConsumer() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ConsumerId, propConfig.getConsumer().getProperty("consumerId"));
        properties.setProperty(PropertyKeyConst.AccessKey, propConfig.getAccessKey());
        properties.setProperty(PropertyKeyConst.SecretKey, propConfig.getSecretKey());
        properties.setProperty(PropertyKeyConst.ONSAddr, propConfig.getOnsAddr());
        properties.setProperty("topic", propConfig.getTopic());
        return new MqConsumer(properties);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnBean(LocalTransactionChecker.class)
    @ConditionalOnProperty(prefix = "aliyun.mq.tranProducer", value = "enabled", havingValue = "true")
    public MqTransactionProducer mqTransactionProducer() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ProducerId, propConfig.getTranProducer().getProperty("producerId"));
        properties.setProperty(PropertyKeyConst.AccessKey, propConfig.getAccessKey());
        properties.setProperty(PropertyKeyConst.SecretKey, propConfig.getSecretKey());
        properties.setProperty(PropertyKeyConst.ONSAddr, propConfig.getOnsAddr());
        properties.setProperty("tranTopic", propConfig.getTranTopic());
        MqTransactionProducer tranProducer = new MqTransactionProducer(properties);
        tranProducer.start(this.localTransactionChecker);
        return tranProducer;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.tranConsumer", value = "enabled", havingValue = "true")
    public MqTransactionConsumer mqTransactionConsumer() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ConsumerId, propConfig.getTranConsumer().getProperty("consumerId"));
        properties.setProperty(PropertyKeyConst.AccessKey, propConfig.getAccessKey());
        properties.setProperty(PropertyKeyConst.SecretKey, propConfig.getSecretKey());
        properties.setProperty(PropertyKeyConst.ONSAddr, propConfig.getOnsAddr());
        properties.setProperty("tranTopic", propConfig.getTranTopic());
        return new MqTransactionConsumer(properties);
    }
}

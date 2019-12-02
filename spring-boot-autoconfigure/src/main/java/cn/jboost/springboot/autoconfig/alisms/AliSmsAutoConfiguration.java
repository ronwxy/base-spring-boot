package cn.jboost.springboot.autoconfig.alisms;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuxk
 * @version V1.0
 * @Title:
 * @Description:
 * @date：2019-9-13 0:07
 */
@Configuration
@EnableConfigurationProperties(AliSmsProperties.class)
@ConditionalOnClass({IAcsClient.class, SendSmsRequest.class})
public class AliSmsAutoConfiguration {
    @Bean
    public AliSmsManager aliSmsManager(AliSmsProperties properties) {
        return new AliSmsManager(properties);
    }
}

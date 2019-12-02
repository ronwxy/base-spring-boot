package cn.jboost.springboot.autoconfig.alioss;

import com.aliyun.oss.OSSClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuxk
 * @version V1.0
 * @Title: 阿里oss配置
 * @Description:
 * @date 2019/9/18 15:17
 */
@Configuration
@EnableConfigurationProperties(AliOssProperties.class)
@ConditionalOnClass(OSSClient.class)
public class AliOssAutoConfiguration {
    @Bean
    public AliOssManager aliOssManager(AliOssProperties properties){
        return new AliOssManager(properties);
    }
}

package cn.jboost.springboot.autoconfig.alioss;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yuxk
 * @version V1.0
 * @Title:  阿里oss基本属性
 * @Description:
 * @date 2019/9/18 15:29
 */
@ConfigurationProperties(prefix = "aliyun.oss")
@Getter
@Setter
public class AliOssProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String bucket;
    private String domain;
}

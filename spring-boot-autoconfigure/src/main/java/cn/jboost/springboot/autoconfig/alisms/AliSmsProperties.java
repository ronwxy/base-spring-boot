package cn.jboost.springboot.autoconfig.alisms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yuxk
 * @version V1.0
 * @Title:
 * @Description:
 * @date：2019-9-13 0:02
 */
@ConfigurationProperties(prefix = "aliyun.sms")
@Setter
@Getter
public class AliSmsProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
}

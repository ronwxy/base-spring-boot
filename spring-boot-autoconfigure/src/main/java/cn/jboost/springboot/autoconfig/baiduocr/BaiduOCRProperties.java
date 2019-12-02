package cn.jboost.springboot.autoconfig.baiduocr;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/***
 *
 * @Author ronwxy
 * @Date 2019/9/23 14:22   
 */
@ConfigurationProperties(prefix = "baidu.ocr")
@Getter
@Setter
public class BaiduOCRProperties {
    private String appId;
    private String apiKey;
    private String secretKey;
    private Integer connectionTimeoutInMillis;
    private Integer socketTimeoutInMillis;

}

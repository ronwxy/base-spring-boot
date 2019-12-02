package cn.jboost.springboot.autoconfig.baiduocr;

import com.baidu.aip.ocr.AipOcr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 *
 * @Author ronwxy
 * @Date 2019/9/23 14:11   
 */
@Configuration
@EnableConfigurationProperties(BaiduOCRProperties.class)
@ConditionalOnClass({AipOcr.class})
public class BaiduOCRAutoConfiguration {

    @Autowired
    private BaiduOCRProperties ocrProperties;

    @Bean
    public AipOcr aipOcr(){
        AipOcr client = new AipOcr(ocrProperties.getAppId(), ocrProperties.getApiKey(), ocrProperties.getSecretKey());
        if(ocrProperties.getConnectionTimeoutInMillis() != null){
            client.setConnectionTimeoutInMillis(ocrProperties.getConnectionTimeoutInMillis());
        }
        if(ocrProperties.getSocketTimeoutInMillis() != null) {
            client.setSocketTimeoutInMillis(ocrProperties.getSocketTimeoutInMillis());
        }
        return client;
    }

}

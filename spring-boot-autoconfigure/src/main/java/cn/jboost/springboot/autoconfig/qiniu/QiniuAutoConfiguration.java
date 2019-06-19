package cn.jboost.springboot.autoconfig.qiniu;

import com.qiniu.util.Auth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ConditionalOnClass(Auth.class)
@ConfigurationProperties(prefix = "qiniu")
public class QiniuAutoConfiguration {

	private String accessKey;

	private String secretKey;

	private String pipeline;

	private String tokenExpiredTime;

	private List<QiniuUtil.QiniuBucket> buckets;


	@PostConstruct
	public void initQiniuUtil() {
		QiniuUtil.init(accessKey, secretKey, pipeline, tokenExpiredTime, buckets);
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

	public String getPipeline() {
		return pipeline;
	}

	public void setPipeline(String pipeline) {
		this.pipeline = pipeline;
	}

	public String getTokenExpiredTime() {
		return tokenExpiredTime;
	}

	public void setTokenExpiredTime(String tokenExpiredTime) {
		this.tokenExpiredTime = tokenExpiredTime;
	}

	public List<QiniuUtil.QiniuBucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<QiniuUtil.QiniuBucket> buckets) {
		this.buckets = buckets;
	}
}

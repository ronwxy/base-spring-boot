package cn.jboost.springboot.autoconfig.aoplog;

import cn.jboost.springboot.autoconfig.aoplog.service.Slf4jLogService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author ronwxy
 * @Date 2020/5/30 18:02
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "aoplog")
@Getter
@Setter
public class AOPLogConfigProperties {
    /**
     * 日志记录实现类，可以自定义
     */
    private Class serviceImplClass = Slf4jLogService.class;
    /**
     * 集合（数组）类型解析元素个数
     */
    private Integer collectionDepthThreshold = 10;
}

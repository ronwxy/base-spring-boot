package cn.jboost.springboot.autoconfig.aoplog;

import cn.hutool.core.util.ReflectUtil;
import cn.jboost.springboot.autoconfig.aoplog.aspect.AOPLogAspect;
import cn.jboost.springboot.autoconfig.aoplog.service.ILogService;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author ronwxy
 * @Date 2020/5/28 20:19
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(AOPLogConfigProperties.class)
@ConditionalOnClass({Aspect.class, MDC.class})
public class AOPLogAutoConfiguration {

    @Autowired
    private AOPLogConfigProperties configProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public AOPLogAspect loggerAspect() {

        ILogService logService;
        try {
            logService = (ILogService) applicationContext.getBean(configProperties.getServiceImplClass());
            if (logService == null) {
                logService = (ILogService) ReflectUtil.newInstance(configProperties.getServiceImplClass());
            }
        } catch (Exception e) {
            logService = (ILogService) ReflectUtil.newInstance(configProperties.getServiceImplClass());
        }
        AOPLogAspect loggerAspect = new AOPLogAspect(logService, configProperties.getCollectionDepthThreshold());
        return loggerAspect;
    }
}

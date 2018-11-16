package com.springboot.boot.dubbo.condition;

import com.springboot.boot.dubbo.FileChecker;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Deprecated
public class DubboConsumerCondition implements Condition {
    private static final String DUBBO_CONSUMER_CONFIG_FILE_PATH_PATTERN =
            "classpath*:META-INF/dubbo-config.xml";
    private static final String DUBBO_CONSUMER_REGISTRY_FILE_PATH_PATTERN =
            "classpath:META-INF/dubbo-config-registry.xml";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return (FileChecker.checkFileExists(DUBBO_CONSUMER_CONFIG_FILE_PATH_PATTERN)
                && FileChecker.checkFileExists(DUBBO_CONSUMER_REGISTRY_FILE_PATH_PATTERN));
    }
}

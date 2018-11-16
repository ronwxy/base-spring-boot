package com.springboot.boot.dubbo.condition;

import com.springboot.boot.dubbo.FileChecker;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Deprecated
public class DubboProviderCondition implements Condition {
    private static final String DUBBO_PROVIDER_CONFIG_FILE_PATH_PATTERN = "classpath:/**/spring-dubbo.xml";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return (FileChecker.checkFileExists(DUBBO_PROVIDER_CONFIG_FILE_PATH_PATTERN));
    }
}
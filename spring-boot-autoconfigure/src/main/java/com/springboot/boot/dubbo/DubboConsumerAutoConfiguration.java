package com.springboot.boot.dubbo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ConditionalOnBean(DubboConsumerMarkerConfiguration.Marker.class)
@ImportResource({"classpath*:META-INF/dubbo-config.xml", "classpath:META-INF/dubbo-config-registry.xml"})
public class DubboConsumerAutoConfiguration {


}
package com.springboot.boot.dubbo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ConditionalOnBean(DubboProviderMarkerConfiguration.Marker.class)
@ImportResource("classpath:**/spring-dubbo.xml")
public class DubboProviderAutoConfiguration {

}
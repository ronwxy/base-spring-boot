package com.springboot.boot.dubbo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboConsumerMarkerConfiguration {
    @Bean
    public Marker dubboConsumerMarkerBean() {
        return new Marker();
    }

    class Marker {
    }
}
package com.springboot.boot.dubbo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboProviderMarkerConfiguration {
    @Bean
    public Marker dubboProviderMarkerBean() {
        return new Marker();
    }

    class Marker {
    }
}
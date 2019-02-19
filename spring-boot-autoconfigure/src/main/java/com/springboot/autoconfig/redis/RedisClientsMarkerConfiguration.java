package com.springboot.autoconfig.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisClientsMarkerConfiguration {
    @Bean
    public Marker redisClientsMarkerBean() {
        return new Marker();
    }

    class Marker {
    }
}
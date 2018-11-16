package com.springboot.boot.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import redis.clients.jedis.Jedis;

import java.util.List;

@Configuration
@ConditionalOnClass({JedisConnection.class, RedisOperations.class, Jedis.class})
@ConditionalOnBean(RedisClientsMarkerConfiguration.Marker.class)
@EnableConfigurationProperties(RedisClientsProperties.class)
public class RedisClientAutoConfiguration {

    private List<RedisClientSpecification> redisClientSpecifications;

    @Autowired(required = false)
    public void setRedisClientSpecifications(List<RedisClientSpecification> redisClientSpecifications) {
        this.redisClientSpecifications = redisClientSpecifications;
    }

    @Bean
    public RedisClientFactory redisClientFactory() {
        RedisClientFactory redisClientFactory = new RedisClientFactory();
        redisClientFactory.setConfigurations(this.redisClientSpecifications);
        return redisClientFactory;
    }
}

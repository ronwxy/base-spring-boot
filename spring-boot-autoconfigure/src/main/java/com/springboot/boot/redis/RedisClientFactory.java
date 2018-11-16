package com.springboot.boot.redis;

import com.springboot.common.NamedContextFactory;

public class RedisClientFactory extends NamedContextFactory<RedisClientSpecification> {
    static final String NAMESPACE = "redisClient";

    public RedisClientFactory() {
        super(RedisClientConfiguration.class, NAMESPACE, "redis.client.name");
    }
}

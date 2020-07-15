package cn.jboost.springboot.autoconfig.redis.clients;

import cn.jboost.springboot.autoconfig.redis.NamedContextFactory;

public class RedisClientsFactory extends NamedContextFactory<RedisClientSpecification> {
    static final String NAMESPACE = "redisClient";

    public RedisClientsFactory() {
        super(RedisClientsConfiguration.class, NAMESPACE, "redis.client.name");
    }
}

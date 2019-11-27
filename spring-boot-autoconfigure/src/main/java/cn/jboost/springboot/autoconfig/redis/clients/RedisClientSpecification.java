package cn.jboost.springboot.autoconfig.redis.clients;

import cn.jboost.springboot.common.NamedContextFactory;

public class RedisClientSpecification implements NamedContextFactory.Specification {
    private String name;
    private Class<?>[] configuration;

    public RedisClientSpecification(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }
}

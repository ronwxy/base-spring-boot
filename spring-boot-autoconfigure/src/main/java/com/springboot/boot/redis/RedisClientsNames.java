package com.springboot.boot.redis;

/**
 * use it together for redis client configuration in both location below;<br>
 * 1.configuration properties or yml<br>
 * eg. redis.client.auth.database=1<br>
 * 2.{@link org.springframework.context.annotation.Configuration}<br>
 * eg.{@code @RedisClient(RedisClientsNames.AUTH)}
 *
 * @author liubo
 */
public interface RedisClientsNames {
    String AUTH = "auth";
}

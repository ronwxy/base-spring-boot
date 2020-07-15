package cn.jboost.springboot.autoconfig.limiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限速注解
 * @Author ronwxy
 * @Date 2020/6/30 18:19
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    String key() default "";
    String prefix() default "rateLimit:"; //key前缀
    int expire() default 60; // 表示令牌桶模型RedisPermits redis key的过期时间/秒
    double rate() default 1.0; // permitsPerSecond值
    double burst() default 1.0; // maxBurstSeconds值
    int timeout() default 0; // 超时时间/秒
    LimitType limitType() default LimitType.METHOD;
}

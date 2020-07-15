package cn.jboost.springboot.autoconfig.limiter.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在方法上的分布式锁注解
 *
 * @Author ronwxy
 * @Date 2020/6/22 16:03
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLockable {
    String key();
    String prefix() default "disLock:";
    long expire() default 10L; // 默认10s过期
}

package cn.jboost.springboot.autoconfig.limiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限量注解
 * @Author ronwxy
 * @Date 2020/6/30 19:33
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CountLimit {
    String key() default "";
    String prefix() default "countLimit:"; //key前缀
    int limit() default 1;  // expire时间段内限制访问次数
    int period() default 1; // 表示时间段/秒
    LimitType limitType() default LimitType.METHOD;
}

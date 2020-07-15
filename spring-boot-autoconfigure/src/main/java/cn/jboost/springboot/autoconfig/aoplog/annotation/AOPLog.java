package cn.jboost.springboot.autoconfig.aoplog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对方法调用进行日志记录的注解，
 * 作用于方法或类上（表示对类里的所有方法有效）
 *
 * @Author ronwxy
 * @Date 2020/5/28 18:35
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AOPLog {

    /**
     * 日志记录点，方法调用时、返回时、或两者
     * 默认为 {@link LogPoint#BOTH}.
     */
    LogPoint logPoint() default LogPoint.BOTH;

    /**
     * 是否对异常进行日志记录，默认记录（如果有统一异常处理，则异常处理可不再打日志）
     * @return
     */
    boolean logException() default true;

}
package cn.jboost.springboot.autoconfig.limiter;

/**
 * @Author ronwxy
 * @Date 2020/7/1 11:17
 * @Version 1.0
 */
public enum LimitType {
    IP,    //根据IP限流
    USER, //根据用户限流
    METHOD, //根据方法名全局限流
    CUSTOM //自定义
}

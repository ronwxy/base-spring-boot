package cn.jboost.springboot.autoconfig.aoplog.annotation;

/**
 * 日志记录点
 *
 * @Author ronwxy
 * @Date 2020/5/28 18:35
 * @Version 1.0
 */
public enum LogPoint {
    /**
     * 调用时
     */
    IN,

    /**
     * 返回时
     */
    OUT,

    /**
     * 包含调用时与返回时
     */
    BOTH
}

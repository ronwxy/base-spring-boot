package cn.jboost.springboot.autoconfig.aoplog.service;

import java.lang.reflect.Method;

/**
 * 日志服务接口
 * @Author ronwxy
 * @Date 2020/5/28 11:39
 * @Version 1.0
 */
public interface ILogService {
    /**
     * 进入方法时记录日志
     */
    void logCall(Method method, String[] argNames, Object[] argValues, int depthThreshold);
                  /**
     * 方法返回前记录日志
     */
    void logReturn(Method method, int argCount, Object result, int depthThreshold);

    /**
     * 抛出异常时记录日志
     */
    void logThrow(Method method, int argCount, Throwable e, boolean stackTrace);
}

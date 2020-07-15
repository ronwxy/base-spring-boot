package cn.jboost.springboot.autoconfig.aoplog.service;

import cn.jboost.springboot.autoconfig.aoplog.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 使用Slf4j记录日志
 *
 * @Author ronwxy
 * @Date 2020/5/28 11:50
 * @Version 1.0
 */
public class Slf4jLogService implements ILogService {

    @Override
    public void logCall(Method method, String[] argNames, Object[] argValues, int depthThreshold) {
        getLogger(method).info(LogUtil.callMessage(method.getName(), argNames, argValues, depthThreshold));
    }

    @Override
    public void logReturn(Method method, int argCount, Object result, int depthThreshold) {
        getLogger(method).info(LogUtil.returnMessage(method.getName(), argCount, result, depthThreshold));
    }

    @Override
    public void logThrow(Method method, int argCount, Throwable e, boolean stackTrace) {
        getLogger(method).error(LogUtil.throwMessage(method.getName(), argCount, e, true));
    }

    private Logger getLogger(Method method) {
        return LoggerFactory.getLogger(method.getDeclaringClass());
    }
}

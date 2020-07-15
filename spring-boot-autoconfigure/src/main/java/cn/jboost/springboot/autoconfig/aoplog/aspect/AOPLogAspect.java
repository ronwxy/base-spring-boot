package cn.jboost.springboot.autoconfig.aoplog.aspect;


import cn.jboost.springboot.autoconfig.aoplog.annotation.AOPLog;
import cn.jboost.springboot.autoconfig.aoplog.annotation.LogPoint;
import cn.jboost.springboot.autoconfig.aoplog.service.ILogService;
import cn.jboost.springboot.autoconfig.aoplog.util.LogConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * 切面日志
 *
 * @Author ronwxy
 * @Date 2020/5/28 18:35
 * @Version 1.0
 */
@Aspect
public class AOPLogAspect {

    private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private ILogService logService;
    private int depthThreshold;

    public AOPLogAspect(ILogService logService, int depthThreshold) {
        this.logService = logService;
        this.depthThreshold = depthThreshold;
    }

    /**
     * 对调用方法进行切面日志记录
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "@within(cn.jboost.springboot.autoconfig.aoplog.annotation.AOPLog)"     // per class
            + " || @annotation(cn.jboost.springboot.autoconfig.aoplog.annotation.AOPLog)")  // per method
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = extractMethod(joinPoint);

        String methodName = method.getName();
        String className = method.getDeclaringClass().getName();
        //将当前类名方法名存入MDC
        MDC.put(LogConstants.CLASS_NAME, className);
        MDC.put(LogConstants.METHOD_NAME, methodName);

        AOPLog logAnnotation = getLogAnnotation(method);
        //参数名称
        String[] argNames = parameterNameDiscoverer.getParameterNames(method);
        //参数值
        Object[] argValues = joinPoint.getArgs();

        if (LogPoint.IN.equals(logAnnotation.logPoint()) || LogPoint.BOTH.equals(logAnnotation.logPoint())) {
            logService.logCall(method, argNames, argValues, depthThreshold);
        }

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            if (logAnnotation.logException()) {
                logService.logThrow(method, argValues.length, e, true);
            }
            throw e;
        }
        //执行时间存入MDC
        MDC.put(LogConstants.ELAPSED_TIME, Long.toString(System.currentTimeMillis() - startTime));

        if (LogPoint.OUT.equals(logAnnotation.logPoint()) || LogPoint.BOTH.equals(logAnnotation.logPoint())) {
            logService.logReturn(method, argValues.length, result, depthThreshold);
        }

        MDC.remove(LogConstants.CLASS_NAME);
        MDC.remove(LogConstants.METHOD_NAME);
        MDC.remove(LogConstants.ELAPSED_TIME);

        return result;
    }


    private AOPLog getLogAnnotation(Method method) {
        AOPLog logAnnotation = method.getAnnotation(AOPLog.class);
        //方法上没有，则获取类上注解
        if (logAnnotation == null) {
            logAnnotation = method.getDeclaringClass().getAnnotation(AOPLog.class);
        }
        return logAnnotation;
    }

    private Method extractMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // signature.getMethod() points to method declared in interface. it is not suit to discover arg names and arg
        // annotations
        // see AopProxyUtils: org.springframework.cache.interceptor.CacheAspectSupport#execute(CacheAspectSupport
        // .Invoker, Object, Method, Object[])
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (Modifier.isPublic(signature.getMethod().getModifiers())) {
            return targetClass.getMethod(signature.getName(), signature.getParameterTypes());
        } else {
            return ReflectionUtils.findMethod(targetClass, signature.getName(), signature.getParameterTypes());
        }
    }

}

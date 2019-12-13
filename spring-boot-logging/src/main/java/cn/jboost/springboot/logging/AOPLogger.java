/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package cn.jboost.springboot.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Logger aspect.
 */
@Aspect
public class AOPLogger implements InitializingBean {
    private static final String ELAPSED_TIME = "elapsedTime";
    private LogAdapter logAdapter;
    private Map<Severity, LogStrategy> logStrategies;
    private final LocalVariableTableParameterNameDiscoverer localVariableNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final ExceptionResolver exceptionResolver = new ExceptionResolver();
    private final ConcurrentMap<Method, MethodDescriptor> cache = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        logStrategies = new EnumMap<>(Severity.class);
        logStrategies.put(Severity.ERROR, new LogStrategy.ErrorLogStrategy(logAdapter));
        logStrategies.put(Severity.WARN, new LogStrategy.WarnLogStrategy(logAdapter));
        logStrategies.put(Severity.INFO, new LogStrategy.InfoLogStrategy(logAdapter));
        logStrategies.put(Severity.DEBUG, new LogStrategy.DebugLogStrategy(logAdapter));
        logStrategies.put(Severity.TRACE, new LogStrategy.TraceLogStrategy(logAdapter));
    }

    public void setLogAdapter(LogAdapter log) {
        this.logAdapter = log;
    }

    /**
     * Advise. Logs the advised method.
     *
     * @param joinPoint represents advised method
     * @return method execution result
     * @throws Throwable in case of exception
     */
    @Around(value = "execution(@(@cn.jboost.springboot.logging.annotation.Logging *) * *.* (..))" // per method
            + " || execution(* (@(@cn.jboost.springboot.logging.annotation.Logging *) *).*(..))"  // per class
            , argNames = "joinPoint")
    public Object logTheMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();
        Logger logger = logAdapter.getLog(joinPoint.getTarget().getClass());
        Method method = extractMethod(joinPoint);

        MethodDescriptor descriptor = getMethodDescriptor(method);
        InvocationDescriptor invocationDescriptor = descriptor.getInvocationDescriptor();

        String methodName = method.getName();
        String callingClassName = method.getDeclaringClass().getName();
        MDC.put("callingClass", callingClassName);
        MDC.put("callingMethod", methodName);

        if (beforeLoggingOn(invocationDescriptor, logger)) {
            ArgumentDescriptor argumentDescriptor = getArgumentDescriptor(descriptor, method, args.length);
            logStrategies.get(invocationDescriptor.getBeforeSeverity()).logBefore(logger, methodName, args, argumentDescriptor);
        }

        long startTime = System.currentTimeMillis();

        Object result;
        if (invocationDescriptor.getExceptionAnnotation() == null) {
            result = joinPoint.proceed(args);
        } else {
            try {
                result = joinPoint.proceed(args);
            } catch (Exception e) {
                long endTime = System.currentTimeMillis();
                MDC.put(ELAPSED_TIME, Long.toString(endTime - startTime));
                ExceptionDescriptor exceptionDescriptor = getExceptionDescriptor(descriptor, invocationDescriptor);
                Class<? extends Exception> resolved = exceptionResolver.resolve(exceptionDescriptor, e);
                if (resolved != null) {
                    ExceptionSeverity excSeverity = exceptionDescriptor.getExceptionSeverity(resolved);
                    if (isLoggingOn(excSeverity.getSeverity(), logger)) {
                        logStrategies.get(excSeverity.getSeverity()).logException(logger, methodName, args.length, e, excSeverity.getStackTrace());
                    }
                }
                throw e;
            }
        }
        long endTime = System.currentTimeMillis();
        MDC.put(ELAPSED_TIME, Long.toString(endTime - startTime));
        if (afterLoggingOn(invocationDescriptor, logger)) {
            Object loggedResult = (method.getReturnType() == Void.TYPE) ? Void.TYPE : result;
            logStrategies.get(invocationDescriptor.getAfterSeverity()).logAfter(logger, methodName, args.length, loggedResult);
        }
        MDC.remove("callingClass");
        MDC.remove("callingMethod");
        MDC.remove(ELAPSED_TIME);

        return result;
    }

    private MethodDescriptor getMethodDescriptor(Method method) {
        MethodDescriptor cached = cache.get(method);
        if (cached != null) {
            return cached;
        }
        cached = new MethodDescriptor(new InvocationDescriptor.Builder(method).build());
        MethodDescriptor prev = cache.putIfAbsent(method, cached);
        return prev == null ? cached : prev;
    }

    private ArgumentDescriptor getArgumentDescriptor(MethodDescriptor descriptor, Method method, int argumentCount) {
        if (descriptor.getArgumentDescriptor() != null) {
            return descriptor.getArgumentDescriptor();
        }
        ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor.Builder(method, argumentCount, localVariableNameDiscoverer).build();
        descriptor.setArgumentDescriptor(argumentDescriptor);
        return argumentDescriptor;
    }

    private ExceptionDescriptor getExceptionDescriptor(MethodDescriptor descriptor, InvocationDescriptor invocationDescriptor) {
        if (descriptor.getExceptionDescriptor() != null) {
            return descriptor.getExceptionDescriptor();
        }
        ExceptionDescriptor exceptionDescriptor = new ExceptionDescriptor.Builder(invocationDescriptor.getExceptionAnnotation()).build();
        descriptor.setExceptionDescriptor(exceptionDescriptor);
        return exceptionDescriptor;
    }

    private Method extractMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // signature.getMethod() points to method declared in interface. it is not suit to discover arg names and arg annotations
        // see AopProxyUtils: org.springframework.cache.interceptor.CacheAspectSupport#execute(CacheAspectSupport.Invoker, Object, Method, Object[])
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (Modifier.isPublic(signature.getMethod().getModifiers())) {
            return targetClass.getMethod(signature.getName(), signature.getParameterTypes());
        } else {
            return ReflectionUtils.findMethod(targetClass, signature.getName(), signature.getParameterTypes());
        }
    }

    private boolean beforeLoggingOn(InvocationDescriptor descriptor, Logger logger) {
        return isLoggingOn(descriptor.getBeforeSeverity(), logger);
    }

    private boolean afterLoggingOn(InvocationDescriptor descriptor, Logger logger) {
        return isLoggingOn(descriptor.getAfterSeverity(), logger);
    }

    private boolean isLoggingOn(Severity severity, Logger logger) {
        return severity != null && logStrategies.get(severity).isLogEnabled(logger);
    }
}

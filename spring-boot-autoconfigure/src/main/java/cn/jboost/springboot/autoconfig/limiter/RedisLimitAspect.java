package cn.jboost.springboot.autoconfig.limiter;


import cn.jboost.springboot.common.exception.ExceptionUtil;
import cn.jboost.springboot.common.util.SecurityUtil;
import cn.jboost.springboot.common.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @Author ronwxy
 * @Date 2020/6/30 19:37
 * @Version 1.0
 */
@Aspect
@Slf4j
public class RedisLimitAspect {
    private static final String LIMIT_MESSAGE = "您的访问过于频繁，请稍后重试";

    private RedisCountLimiter redisCountLimiter;
    private RedisRateLimiterFactory redisRateLimiterFactory;

    public RedisLimitAspect(RedisCountLimiter redisCountLimiter, RedisRateLimiterFactory redisRateLimiterFactory) {
        this.redisCountLimiter = redisCountLimiter;
        this.redisRateLimiterFactory = redisRateLimiterFactory;
    }

    @Before(value = "@annotation(countLimit)")
    public void countLimit(JoinPoint  point, CountLimit countLimit) throws Throwable {
        String key = getKey(point, countLimit.limitType(), countLimit.key(), countLimit.prefix());
        if (!redisCountLimiter.tryAcquire(key, countLimit.limit(), countLimit.period())) {
            ExceptionUtil.rethrowClientSideException(LIMIT_MESSAGE);
        }
    }

    @Before(value = "@annotation(rateLimit)")
    public void rateLimit(JoinPoint  point, RateLimit rateLimit) throws Throwable {
        String key = getKey(point, rateLimit.limitType(), rateLimit.key(), rateLimit.prefix());
        RedisRateLimiter redisRateLimiter = redisRateLimiterFactory.build(key, rateLimit.rate(), rateLimit.burst(), rateLimit.expire());
        if(!redisRateLimiter.tryAcquire(key, rateLimit.timeout(), TimeUnit.SECONDS)){
            ExceptionUtil.rethrowClientSideException(LIMIT_MESSAGE);
        }
    }

    private String getKey(JoinPoint  point, LimitType limitType, String originKey, String prefix) {
        String key;
        switch (limitType) {
            case IP:
                key = WebUtil.getIP();
                break;
            case USER:
                Long userId = SecurityUtil.getUserId();
                if(userId == null) {
                    ExceptionUtil.rethrowClientSideException("获取不到用户信息");
                }
                key = String.valueOf(userId);
                break;
            case METHOD:
                key = point.getTarget().getClass().getName() + ":" + ((MethodSignature) point.getSignature()).getMethod().getName();
                break;
            case CUSTOM:
                key = String.valueOf(resolve(point, originKey));
                break;
            default:
                key = "";
        }
        if (StringUtils.isBlank(key)) {
            ExceptionUtil.rethrowClientSideException("Key不能为空");
        }
        return prefix + key;
    }

    public Object resolve(JoinPoint joinPoint, String placeholder) {

        if (StringUtils.isBlank(placeholder)) {
            return null;
        }

        Object value = null;
        if (placeholder.matches("#\\{\\D*\\}")) {// 匹配上#{},则把内容当作变量
            String param = placeholder.replaceAll("#\\{", "").replaceAll("\\}", "");
            if (param.contains(".")) { // 多层引用
                try {
                    value = complexResolve(joinPoint, param);
                } catch (Exception e) {
                    log.error("fail to resolve value for {}", param, e);
                }
            } else {
                value = simpleResolve(joinPoint, param);
            }
        } else {
            value = placeholder;
        }
        return value;
    }

    /**
     * 多层引用参数解析值
     *
     * @param joinPoint
     * @param placeholder
     * @return
     * @throws Exception
     */
    private Object complexResolve(JoinPoint joinPoint, String placeholder) throws Exception {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] names = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        String[] params = placeholder.split("\\.");

        for (int i = 0; i < names.length; i++) {
            if (params[0].equals(names[i])) {
                Object obj = args[i];
                Method getMethod = obj.getClass().getDeclaredMethod(getMethodName(params[1]), null);
                Object value = getMethod.invoke(args[i]);
                return getValue(value, 1, params);
            }
        }

        return null;

    }

    /**
     * 简单参数获取值
     *
     * @param joinPoint
     * @param placeholder
     * @return
     */
    private Object simpleResolve(JoinPoint joinPoint, String placeholder) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] names = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < names.length; i++) {
            if (placeholder.equals(names[i])) {
                return args[i];
            }
        }
        return null;
    }

    private Object getValue(Object obj, int index, String[] params) throws Exception {
        if (obj != null && index < params.length - 1) {
            Method method = obj.getClass().getDeclaredMethod(getMethodName(params[index + 1]), null);
            obj = method.invoke(obj);
            getValue(obj, index + 1, params);
        }
        return obj;
    }

    private String getMethodName(String name) {
        return "get" + StringUtils.capitalize(name);
    }
}

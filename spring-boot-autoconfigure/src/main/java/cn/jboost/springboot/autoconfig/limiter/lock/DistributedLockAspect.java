package cn.jboost.springboot.autoconfig.limiter.lock;

import cn.jboost.springboot.common.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @Author ronwxy
 * @Date 2020/6/22 16:03
 * @Version 1.0
 */
@Aspect
@Slf4j
public class DistributedLockAspect {

    private DistributedLock lock;

    public DistributedLockAspect(DistributedLock lock) {
        this.lock = lock;
    }

    /**
     * 在方法上执行同步锁
     */
    @Around(value = "@annotation(lockable)")
    public Object distLock(ProceedingJoinPoint point, DistributedLockable lockable) throws Throwable {
        boolean locked = false;
        String key = lockable.prefix() + lockable.key();
        try {
            locked = lock.lock(key, WebUtil.getRequestId(), lockable.expire());
            if(locked) {
                return point.proceed();
            } else {
                log.info("Did not get a lock for key {}", key);
                return null;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if(locked) {
                if(!lock.unLock(key, WebUtil.getRequestId())){
                    log.warn("Unlock {} failed, maybe locked by another client already. ", lockable.key());
                }
            }
        }
    }
}

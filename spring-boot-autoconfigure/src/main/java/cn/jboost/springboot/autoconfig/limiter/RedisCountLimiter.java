package cn.jboost.springboot.autoconfig.limiter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;

/**
 * 限量控制
 * @Author ronwxy
 * @Date 2020/6/30 18:25
 * @Version 1.0
 */
public class RedisCountLimiter {

    private StringRedisTemplate stringRedisTemplate;

    private static final String LUA_SCRIPT = "local c \nc = redis.call('get',KEYS[1]) \nif c and redis.call('incr',KEYS[1]) > tonumber(ARGV[1]) then return 0 end"
            + " \nif c then return 1 else \nredis.call('set', KEYS[1], 1) \nredis.call('expire', KEYS[1], tonumber(ARGV[2])) \nreturn 1 end";

    private static final int SUCCESS_RESULT = 1;
    private static final int FAIL_RESULT = 0;

    public RedisCountLimiter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 是否允许访问
     *
     * @param key redis key
     * @param limit 限制次数
     * @param expire 时间段/秒
     * @return 获取成功true，否则false
     * @throws IllegalArgumentException
     */
    public boolean tryAcquire(String key, int limit, int expire) throws IllegalArgumentException {
        RedisScript<Number> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Number.class);
        Number result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key), String.valueOf(limit), String.valueOf(expire));
        if(result != null && result.intValue() == SUCCESS_RESULT) {
            return true;
        }
        return false;
    }

}

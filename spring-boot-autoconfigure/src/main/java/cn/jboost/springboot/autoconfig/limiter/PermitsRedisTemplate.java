package cn.jboost.springboot.autoconfig.limiter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;

/**
 * 操作令牌数据的RedisTemplate
 *
 * @Author ronwxy
 * @Date 2020/6/22 15:11
 * @Version 1.0
 */
@Slf4j
public class PermitsRedisTemplate extends RedisTemplate<String, RedisPermits> {
    private ObjectMapper objectMapper = new ObjectMapper();

    public PermitsRedisTemplate() {
        super();
        this.setKeySerializer(new StringRedisSerializer());
        this.setValueSerializer(new RedisSerializer<RedisPermits>() {

            @Override
            public byte[] serialize(RedisPermits redisPermits) throws SerializationException {
                try {
                    return objectMapper.writeValueAsBytes(redisPermits);
                } catch (JsonProcessingException e) {
                    log.error("fail to serialize redisPermits. ", e);
                    return null;
                }
            }

            @Override
            public RedisPermits deserialize(byte[] bytes) throws SerializationException {
                if (bytes != null) {
                    try {
                        return objectMapper.readValue(bytes, RedisPermits.class);
                    } catch (IOException e) {
                        log.error("fail to deSerialize to RedisPermits. ", e);
                    }
                }
                return null;
            }
        });
    }
}

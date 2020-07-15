package cn.jboost.springboot.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @Author ronwxy
 * @Date 2020/5/21 18:00
 * @Version 1.0
 */
public class LocalDateTimeUtil {

    /**
     * 获取时间戳，单位为秒
     *
     * @return
     */
    public static long getEpochSecond() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 获取时间戳，单位为毫秒
     *
     * @return
     */
    public static long getEpochMilli() {
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 秒转换localDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime epochSecondToLocalDateTime(long timestamp) {
        LocalDateTime localDateTime = Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 毫秒转换localDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime epochMilliToLocalDateTime(long timestamp) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        return localDateTime;
    }
}

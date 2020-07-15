package cn.jboost.springboot.autoconfig.tkmapper.util;

import org.springframework.boot.json.JacksonJsonParser;

/**
 * @Author ronwxy
 * @Date 2020/7/15 16:37
 * @Version 1.0
 */
public class JsonUtil {

    public static Object parseJson(String json) {
        if (json != null && !"".equals(json)) {
            JacksonJsonParser parser = new JacksonJsonParser();
            if (json.startsWith("{")) {
                return parser.parseMap(json);
            } else if (json.startsWith("[")) {
                return parser.parseList(json);
            } else {
                return null;
            }
        }
        return null;
    }
}

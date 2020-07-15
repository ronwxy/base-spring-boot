package cn.jboost.springboot.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 返回消息体结构封装
 *
 * @Author ronwxy
 * @Date 2020/5/21 18:00
 * @Version 1.0
 */
@Data
public class ResponseWrapper {

    private int code = HttpStatus.BAD_REQUEST.value();
    private Long timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trace;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    private ResponseWrapper() {
        this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    }

    public ResponseWrapper(int code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public ResponseWrapper(int status, String message, String trace) {
        this(status, message);
        this.trace = trace;
    }

    public ResponseWrapper(int status, String message, Object data) {
        this(status, message);
        this.data = data;
    }

    public static ResponseWrapper ok() {
        return ok(null);
    }

    public static ResponseWrapper ok(Object data) {
        return new ResponseWrapper(HttpStatus.OK.value(), "操作成功", data);
    }
    public static ResponseWrapper fail(String msg) { return new ResponseWrapper(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg); }
}

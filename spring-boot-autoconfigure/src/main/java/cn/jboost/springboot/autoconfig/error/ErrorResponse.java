package cn.jboost.springboot.autoconfig.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class ErrorResponse {

    private int status = HttpStatus.BAD_REQUEST.value();
    private Long timestamp;
    private String message;
    private String trace;

    private ErrorResponse() {
        this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    }

    public ErrorResponse(int status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(int status, String message, String trace) {
        this(status, message);
        this.trace = trace;
    }
}

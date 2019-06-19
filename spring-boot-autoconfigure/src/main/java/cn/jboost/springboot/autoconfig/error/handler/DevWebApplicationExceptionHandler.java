package cn.jboost.springboot.autoconfig.error.handler;

import cn.jboost.springboot.autoconfig.error.exception.BizException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin
@RestControllerAdvice
public class DevWebApplicationExceptionHandler extends BaseWebApplicationExceptionHandler {

    @Override
    protected ResponseEntity<Object> asResponseEntity(HttpStatus status, String errorCode, String errorMessage, Exception ex) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(BizException.ERROR_CODE, errorCode);
        data.put(BizException.ERROR_MESSAGE, ex.toString());
        return new ResponseEntity<>(data, status);
    }
}

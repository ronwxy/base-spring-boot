package cn.jboost.springboot.autoconfig.error.handler;

import cn.jboost.springboot.autoconfig.error.ErrorResponse;
import cn.jboost.springboot.common.exception.BizException;
import cn.jboost.springboot.common.exception.CommonErrorCodeEnum;
import cn.jboost.springboot.common.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

/**
 * 统一异常处理类
 */
@CrossOrigin
@RestControllerAdvice
@Slf4j
public class BaseWebApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private boolean includeStackTrace;

    public BaseWebApplicationExceptionHandler(boolean includeStackTrace) {
        super();
        this.includeStackTrace = includeStackTrace;
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Object> handleBizException(BizException ex) {
        logger.warn("catch biz exception: " + ex.toString(), ex.getCause());
        return this.asResponseEntity(HttpStatus.valueOf(ex.getStatus()), ex.getMessage(), ex);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(Exception ex) {
        logger.warn("catch illegal exception.", ex);
        return this.asResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        logger.error("catch exception.", ex);
        return this.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorCodeEnum.INNER_ERROR.getMessage(), ex);
    }

    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        logger.warn("catch unchecked exception.", ex);
        return this.asResponseEntity(status, ex.getMessage(), ex);
    }

    protected ResponseEntity<Object> asResponseEntity(HttpStatus status, String message, Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
        //是否包含异常的stack trace
        if (includeStackTrace) {
            errorResponse.setTrace(ExceptionUtil.extractStackTrace(ex));
        }
        return new ResponseEntity<>(errorResponse, status);
    }


}

package cn.jboost.springboot.autoconfig.error.handler;

import cn.jboost.springboot.common.exception.BizException;
import cn.jboost.springboot.common.exception.ExceptionConstants;
import cn.jboost.springboot.common.exception.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.LinkedHashMap;
import java.util.Map;

/**

 */
@CrossOrigin
@RestControllerAdvice
public class BaseWebApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private boolean includeStackTrace;

    public BaseWebApplicationExceptionHandler(boolean includeStackTrace){
        super();
        this.includeStackTrace = includeStackTrace;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Object> handleBizException(BizException ex) {
        logger.warn("catch biz exception: " + ex.toString(), ex.getCause());
        return this.asResponseEntity(HttpStatus.valueOf(ex.getHttpStatus()), ex.getErrorCode(), ex.getErrorMessage(), ex);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(Exception ex) {
        logger.warn("catch illegal exception.", ex);
        return this.asResponseEntity(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.name().toLowerCase(), ex.getMessage(), ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        logger.error("catch exception.", ex);
        return this.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.name().toLowerCase(), ExceptionConstants.INNER_SERVER_ERROR_MSG, ex);
    }

    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        logger.warn("catch uncustom exception.", ex);
        return this.asResponseEntity(status, status.name().toLowerCase(), ex.getMessage(), ex);
    }

    protected ResponseEntity<Object> asResponseEntity(HttpStatus status, String errorCode, String errorMessage, Exception ex) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(ExceptionConstants.ERROR_CODE_KEY, errorCode);
        data.put(ExceptionConstants.ERROR_MESSAGE_KEY, errorMessage);
        //是否包含异常的stack trace
        if(includeStackTrace){
            ExceptionUtil.addStackTrace(data, ex);
        }
        return new ResponseEntity<>(data, status);
    }


}

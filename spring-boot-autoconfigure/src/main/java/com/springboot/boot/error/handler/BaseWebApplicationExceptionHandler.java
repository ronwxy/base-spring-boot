package com.springboot.boot.error.handler;

import com.springboot.boot.error.exception.BizException;
import com.springboot.boot.error.exception.ExceptionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Application must extends this basic class and annotated with {@link org.springframework.web.bind.annotation.ControllerAdvice}
 * and {@link org.springframework.web.bind.annotation.ResponseBody} to support restful api,or use the {@link org.springframework.web.bind.annotation.RestControllerAdvice} instead;
 * like this
 * <pre>{@code
 *     {@literal @}ControllerAdvice
 *     {@literal @}ResponseBody
 *     public static class GlobalExceptionHandler extends BaseWebApplicationExceptionHandler{
 * <p>
 *     	{@literal @}ExceptionHandler({IllegalArgumentException,IllegalStateException})
 *     	public ResponseEntity<Object> handleXXXException(Exception ex){
 *     		return asResponseEntity(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
 *     }
 * }
 * </pre>
 */
public abstract class BaseWebApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected ResponseEntity<Object> handleNoSuchRequestHandlingMethod(org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException ex,
                                                                       HttpHeaders headers, HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.NOT_FOUND.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.METHOD_NOT_ALLOWED.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers, HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.UNSUPPORTED_MEDIA_TYPE.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                      HttpHeaders headers, HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.NOT_ACCEPTABLE.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {

        return asResponseEntityAndLog(status, HttpStatus.BAD_REQUEST.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.BAD_REQUEST.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatus status, WebRequest request) {

        return asResponseEntityAndLog(status, HttpStatus.BAD_REQUEST.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.BAD_REQUEST.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.BAD_REQUEST.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                                                                     HttpHeaders headers, HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.BAD_REQUEST.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.BAD_REQUEST.name().toLowerCase(), ex);
    }

    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return asResponseEntityAndLog(status, HttpStatus.NOT_FOUND.name().toLowerCase(), ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        logger.error("catch exception.", ex);
        return this.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.name().toLowerCase(), ExceptionConstants.INNER_SERVER_ERROR_MSG, ex);
    }

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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex) {
        logger.warn("catch access denied exception.", ex);
        return this.asResponseEntity(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.name().toLowerCase(), ex.getMessage(), ex);
    }


    protected ResponseEntity<Object> asResponseEntity(HttpStatus status, String errorCode, String errorMessage, Exception ex) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(BizException.ERROR_CODE, errorCode);
        data.put(BizException.ERROR_MESSAGE, errorMessage);
        return new ResponseEntity<>(data, status);
    }

    protected ResponseEntity<Object> asResponseEntityAndLog(HttpStatus status, String errorCode, Exception ex) {
        logger.warn("catch uncustom exception.", ex);
        return this.asResponseEntity(status, errorCode, ex.getMessage(), ex);
    }


}

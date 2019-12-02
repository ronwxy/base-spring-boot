package cn.jboost.springboot.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 访问未授权异常，返回401
 * @author ray4work@126.com
 * @date 2018/6/5 16:23
 */
public class UnauthorizedException extends BizException {

    public UnauthorizedException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, message, cause);
    }
}

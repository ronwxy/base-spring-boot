package cn.jboost.springboot.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 访问未授权异常，返回401
 * @author ray4work@126.com
 * @date 2018/6/5 16:23
 */
public class UnauthorizedException extends BizException {

    public <E extends Enum<E> & BaseErrors> UnauthorizedException(E exceptionCode, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, exceptionCode, cause);
    }

    public <E extends Enum<E> & BaseErrors> UnauthorizedException(E exceptionCode) {
        super(HttpStatus.UNAUTHORIZED, exceptionCode, null);
    }
}

package com.springboot.autoconfig.error.exception;

import com.springboot.autoconfig.error.BaseErrors;
import org.springframework.http.HttpStatus;

/**
 * @author ray4work@126.com
 * @date 2018/6/5 16:23
 */
public class UnauthorizedException extends BizException {

    public <E extends Enum<E> & BaseErrors> UnauthorizedException(E exceptionCode, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, exceptionCode, cause);
    }
}

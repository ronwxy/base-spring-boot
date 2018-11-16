package com.springboot.boot.error.exception;

import com.springboot.boot.error.BaseErrors;
import org.springframework.http.HttpStatus;

/**
 * @author ray4work@126.com
 * @date 2018/6/5 16:26
 */
public class ForbiddenException extends BizException {

    public <E extends Enum<E> & BaseErrors> ForbiddenException(E exceptionCode, Throwable cause) {
        super(HttpStatus.FORBIDDEN, exceptionCode, cause);
    }
}

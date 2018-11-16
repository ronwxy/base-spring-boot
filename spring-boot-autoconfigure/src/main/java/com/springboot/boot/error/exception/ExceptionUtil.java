package com.springboot.boot.error.exception;

import com.springboot.boot.error.BaseErrors;

/**
 * 异常工具类
 *
 * @author ray4work@126.com
 * @date 2018/6/5 18:11
 */
public class ExceptionUtil {

    public static <E extends Enum<E> & BaseErrors> void rethrowClientSideException(E exceptionCode, Throwable cause) {
        throw new ClientSideException(exceptionCode, cause);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowForbiddenException(E exceptionCode, Throwable cause) {
        throw new ForbiddenException(exceptionCode, cause);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowServerSideException(E exceptionCode, Throwable cause) {
        throw new ServerSideException(exceptionCode, cause);
    }

    public static <E extends Enum<E> & BaseErrors> void rethrowUnauthorizedException(E exceptionCode, Throwable cause) {
        throw new UnauthorizedException(exceptionCode, cause);
    }
}

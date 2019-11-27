package cn.jboost.springboot.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 服务端内部异常，返回500
 * @author ray4work@126.com
 * @date 2018/6/5 16:27
 */
public class ServerSideException extends BizException {

    public <E extends Enum<E> & BaseErrors> ServerSideException(E exceptionCode, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, exceptionCode, cause);
    }

    public <E extends Enum<E> & BaseErrors> ServerSideException(E exceptionCode) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, exceptionCode, null);
    }
}

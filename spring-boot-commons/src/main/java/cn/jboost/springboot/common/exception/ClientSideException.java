package cn.jboost.springboot.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 客户端问题导致的异常，如参数校验失败，返回400
 * @author ray4work@126.com
 * @date 2018/6/5 16:17
 */
public class ClientSideException extends BizException {

    public ClientSideException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}

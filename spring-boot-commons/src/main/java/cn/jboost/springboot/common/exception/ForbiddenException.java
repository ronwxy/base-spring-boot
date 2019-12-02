package cn.jboost.springboot.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 禁止访问异常，返回403
 * @author ray4work@126.com
 * @date 2018/6/5 16:26
 */
public class ForbiddenException extends BizException {

    public ForbiddenException(String message, Throwable cause) {
        super(HttpStatus.FORBIDDEN, message, cause);
    }
}

package com.springboot.autoconfig.error.exception;

import com.springboot.autoconfig.error.BaseErrors;
import org.springframework.http.HttpStatus;

public class BizException extends RuntimeException {
    public static final String ERROR_MESSAGE = ExceptionConstants.ERROR_MESSAGE_KEY;
    public static final String ERROR_CODE = ExceptionConstants.ERROR_CODE_KEY;
    private int httpStatus;
    private String errorCode;
    private String errorMessage;

    public BizException(String message) {
        super(message);
        init(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        init(message);
    }

    public BizException(Throwable cause) {
        super(cause);
        init(HttpStatus.INTERNAL_SERVER_ERROR.name().toLowerCase());
    }

    public <E extends Enum<E> & BaseErrors> BizException(HttpStatus status, E exceptionCode, Throwable cause) {
        super(cause);
        this.httpStatus = status.value();
        this.errorCode = exceptionCode.getCode();
        this.errorMessage = exceptionCode.getMsg();
    }


    public BizException(int status, String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.httpStatus = status;
        this.errorCode = errorCode;
    }

    public BizException(HttpStatus status, String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.httpStatus = status.value();
        this.errorCode = errorCode;
    }

    public BizException(int status, String errorCode, String errorMessage, Throwable ex) {
        super(errorMessage, ex);
        this.errorMessage = errorMessage;
        this.httpStatus = status;
        this.errorCode = errorCode;
    }

    public BizException(HttpStatus status, String errorCode, String errorMessage, Throwable ex) {
        super(errorMessage, ex);
        this.errorMessage = errorMessage;
        this.httpStatus = status.value();
        this.errorCode = errorCode;
    }

    private void init(String message) {
        this.errorMessage = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.errorCode = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String toString() {
        return "[error_code: " + this.errorCode + ", error_message: " + this.errorMessage + "]";
    }

    enum Type implements BaseErrors {
        apple("apple", "apple");
        private String code;
        private String msg;

        private Type(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @Override
        public String getCode() {
            return this.code;
        }

        @Override
        public String getMsg() {
            return this.msg;
        }
    }
}

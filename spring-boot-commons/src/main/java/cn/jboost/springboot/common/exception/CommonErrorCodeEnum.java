package cn.jboost.springboot.common.exception;

public enum CommonErrorCodeEnum implements BaseErrors{
    NOT_EXIST("not_exist", "请求的资源不存在"),
    VALIDATE_FAIL("validate_fail", "参数校验失败"),
    TOKEN_EXPIRED("token_expired", "登录状态已过期")
    ;


    private String code;
    private String msg;

    private CommonErrorCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}

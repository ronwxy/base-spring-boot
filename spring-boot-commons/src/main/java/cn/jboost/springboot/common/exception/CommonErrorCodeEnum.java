package cn.jboost.springboot.common.exception;

public enum CommonErrorCodeEnum {
    NOT_EXIST("请求的资源不存在"),
    VALIDATE_FAIL("参数校验失败"),
    TOKEN_EXPIRED("登录状态已过期"),
    INNER_ERROR("抱歉，服务出错啦，请稍后重试"),
    TIMEOUT_ERROR("请求服务超时，请稍后重试"),
    SORT_ERROR("排序格式不符合规范，示例：sort=createTime,asc,age,desc"),
    SORT_LIMIT("升序、降序部分最多均只支持两个字段"),
    COLUMN_ABSENT("不存在对应的列")
    ;

    private String message;

    private CommonErrorCodeEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

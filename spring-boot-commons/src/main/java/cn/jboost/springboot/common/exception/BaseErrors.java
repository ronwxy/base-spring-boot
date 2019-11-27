package cn.jboost.springboot.common.exception;

/**
 * 定义异常code，message的枚举需实现该接口，参考 {@link CommonErrorCodeEnum}
 *
 * @author liubo
 */
public interface BaseErrors {
    String getCode();

    String getMsg();
}

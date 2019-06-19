package cn.jboost.springboot.autoconfig.error;

/**
 * all error enum class must extends this interface;
 *
 * @author liubo
 */
public interface BaseErrors {
    String getCode();

    String getMsg();
}

package com.springboot.boot.error;

/**
 * all error enum class must extends this interface;
 *
 * @author liubo
 */
public interface BaseErrors {
    String getCode();

    String getMsg();
}

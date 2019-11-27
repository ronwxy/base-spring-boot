package cn.jboost.springboot.common.exception;

/**
 * @author ray4work@126.com
 * @date 2018/7/4 16:32
 */
public interface ExceptionConstants {
    String ERROR_CODE_KEY = "error_code";
    String ERROR_MESSAGE_KEY = "error_message";
    String ERROR_TRACE_KEY = "error_trace";

    String INNER_SERVER_ERROR_MSG = "抱歉，服务出错啦，请稍后重试";
    String TIMEOUT_ERROR_MSG = "请求服务超时，请稍后重试";
}

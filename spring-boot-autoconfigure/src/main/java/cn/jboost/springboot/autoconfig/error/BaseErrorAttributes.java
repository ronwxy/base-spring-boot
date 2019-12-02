package cn.jboost.springboot.autoconfig.error;

import cn.hutool.core.bean.BeanUtil;
import cn.jboost.springboot.common.exception.ExceptionUtil;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import java.util.Map;

/***
 * 自定义ErrorAttributes，定制返回格式
 * @Author ronwxy
 * @Date
 */
public class BaseErrorAttributes extends DefaultErrorAttributes {

    private boolean includeStackTrace;

    public BaseErrorAttributes(boolean includeStackTrace) {
        super();
        this.includeStackTrace = includeStackTrace;
    }

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        int status = getStatus(webRequest);
        Throwable error = getErrorDetail(webRequest);
        String message = "";
        String trace = "";
        if (error != null) {
            message = getErrorMessage(error);
            if (this.includeStackTrace) {
                trace = ExceptionUtil.extractStackTrace(error);
            }
        }
        String msgAttr = getAttribute(webRequest, "javax.servlet.error.message");
        if ((!StringUtils.isEmpty(msgAttr) || "".equals(message))
                && !(error instanceof BindingResult)) {
            message = StringUtils.isEmpty(msgAttr) ? "No message available" : msgAttr;
        }
        ErrorResponse errorResponse = new ErrorResponse(status, message, trace);
        return BeanUtil.beanToMap(errorResponse);
    }

    private int getStatus(RequestAttributes requestAttributes) {
        Integer status = getAttribute(requestAttributes,
                "javax.servlet.error.status_code");
        return (status == null ? HttpStatus.BAD_REQUEST.value() : status);
    }

    private Throwable getErrorDetail(WebRequest webRequest) {
        Throwable error = getError(webRequest);
        if (error != null) {
            while (error instanceof ServletException && error.getCause() != null) {
                error = error.getCause();
            }
        }
        return error;
    }

    private String getErrorMessage(Throwable error) {
        BindingResult result = extractBindingResult(error);
        if (result == null) {
            return error.getMessage();
        }
        if (result.getErrorCount() > 0) {
            return "Validation failed for object='" + result.getObjectName()
                    + "'. Error count: " + result.getErrorCount();
        } else {
            return "No errors";
        }
    }

    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof BindingResult) {
            return (BindingResult) error;
        }
        if (error instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) error).getBindingResult();
        }
        return null;
    }

    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

}

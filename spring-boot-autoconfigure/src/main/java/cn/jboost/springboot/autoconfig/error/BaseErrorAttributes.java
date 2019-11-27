package cn.jboost.springboot.autoconfig.error;

import cn.jboost.springboot.common.exception.ExceptionConstants;
import cn.jboost.springboot.common.exception.ExceptionUtil;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import java.util.LinkedHashMap;
import java.util.Map;

/***
* 自定义ErrorAttributes，定制返回格式
* @Author ronwxy
* @Date
*/
public class BaseErrorAttributes extends DefaultErrorAttributes {

    private boolean includeStackTrace;

    public BaseErrorAttributes(boolean includeStackTrace){
        super();
        this.includeStackTrace = includeStackTrace;
    }

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = new LinkedHashMap<String, Object>();
        addStatus(errorAttributes, webRequest);
        addErrorDetails(errorAttributes, webRequest, this.includeStackTrace);
        return errorAttributes;
    }

    private void addErrorMessage(Map<String, Object> errorAttributes, Throwable error) {
        BindingResult result = extractBindingResult(error);
        if (result == null) {
            errorAttributes.put(ExceptionConstants.ERROR_MESSAGE_KEY, error.getMessage());
            return;
        }
        if (result.getErrorCount() > 0) {
            errorAttributes.put(ExceptionConstants.ERROR_MESSAGE_KEY,
                    "Validation failed for object='" + result.getObjectName()
                            + "'. Error count: " + result.getErrorCount());
        } else {
            errorAttributes.put(ExceptionConstants.ERROR_MESSAGE_KEY, "No errors");
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

    private void addStatus(Map<String, Object> errorAttributes,
                           RequestAttributes requestAttributes) {
        Integer status = getAttribute(requestAttributes,
                "javax.servlet.error.status_code");
        if (status == null) {
            errorAttributes.put(ExceptionConstants.ERROR_CODE_KEY, "None");
            return;
        }
        try {
            errorAttributes.put(ExceptionConstants.ERROR_CODE_KEY, HttpStatus.valueOf(status).getReasonPhrase());
        } catch (Exception ex) {
            errorAttributes.put(ExceptionConstants.ERROR_CODE_KEY, "Http Status " + status);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    private void addErrorDetails(Map<String, Object> errorAttributes,
                                 WebRequest webRequest, boolean includeStackTrace) {
        Throwable error = getError(webRequest);
        if (error != null) {
            while (error instanceof ServletException && error.getCause() != null) {
                error = error.getCause();
            }
            addErrorMessage(errorAttributes, error);
            if (includeStackTrace) {
                ExceptionUtil.addStackTrace(errorAttributes, error);
            }
        }
        Object message = getAttribute(webRequest, "javax.servlet.error.message");
        if ((!StringUtils.isEmpty(message) || errorAttributes.get(ExceptionConstants.ERROR_MESSAGE_KEY) == null)
                && !(error instanceof BindingResult)) {
            errorAttributes.put(ExceptionConstants.ERROR_MESSAGE_KEY,
                    StringUtils.isEmpty(message) ? "No message available" : message);
        }
    }

}

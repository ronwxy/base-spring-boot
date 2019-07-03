package cn.jboost.springboot.autoconfig.error;

import cn.jboost.springboot.autoconfig.error.exception.BizException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
            errorAttributes.put(BizException.ERROR_MESSAGE, error.getMessage());
            return;
        }
        if (result.getErrorCount() > 0) {
            errorAttributes.put(BizException.ERROR_MESSAGE,
                    "Validation failed for object='" + result.getObjectName()
                            + "'. Error count: " + result.getErrorCount());
        } else {
            errorAttributes.put(BizException.ERROR_MESSAGE, "No errors");
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
            errorAttributes.put(BizException.ERROR_CODE, "None");
            return;
        }
        try {
            errorAttributes.put(BizException.ERROR_CODE, HttpStatus.valueOf(status).getReasonPhrase());
        } catch (Exception ex) {
            errorAttributes.put(BizException.ERROR_CODE, "Http Status " + status);
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
                addStackTrace(errorAttributes, error);
            }
        }
        Object message = getAttribute(webRequest, "javax.servlet.error.message");
        if ((!StringUtils.isEmpty(message) || errorAttributes.get(BizException.ERROR_MESSAGE) == null)
                && !(error instanceof BindingResult)) {
            errorAttributes.put(BizException.ERROR_MESSAGE,
                    StringUtils.isEmpty(message) ? "No message available" : message);
        }
    }

    private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        errorAttributes.put(BizException.ERROR_TRACE, stackTrace.toString());
    }
}

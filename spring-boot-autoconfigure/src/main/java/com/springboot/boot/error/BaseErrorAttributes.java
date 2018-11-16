package com.springboot.boot.error;

import com.springboot.boot.error.exception.BizException;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;

import javax.servlet.ServletException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = new LinkedHashMap<String, Object>();
        addStatus(errorAttributes, requestAttributes);
        addErrorDetails(errorAttributes, requestAttributes, includeStackTrace);
        return errorAttributes;
    }

    private void addErrorMessage(Map<String, Object> errorAttributes, Throwable error) {
        BindingResult result = extractBindingResult(error);
        if (result == null) {
            errorAttributes.put(BizException.ERROR_MESSAGE, error.getMessage());
            return;
        }
        if (result.getErrorCount() > 0) {
//            errorAttributes.put("errors", result.getAllErrors());
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
//            errorAttributes.put("status", 999);
            errorAttributes.put("error", "None");
            return;
        }
//        errorAttributes.put("status", status);
        try {
            errorAttributes.put(BizException.ERROR_CODE, HttpStatus.valueOf(status).getReasonPhrase());
        } catch (Exception ex) {
            // Unable to obtain a reason
            errorAttributes.put(BizException.ERROR_CODE, "Http Status " + status);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    private void addErrorDetails(Map<String, Object> errorAttributes,
                                 RequestAttributes requestAttributes, boolean includeStackTrace) {
        Throwable error = getError(requestAttributes);
        if (error != null) {
            while (error instanceof ServletException && error.getCause() != null) {
                error = error.getCause();
            }
//            errorAttributes.put("error", error.getClass().getName());
            addErrorMessage(errorAttributes, error);
            if (includeStackTrace) {
                addStackTrace(errorAttributes, error);
            }
        }
        Object message = getAttribute(requestAttributes, "javax.servlet.error.message");
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
        errorAttributes.put("trace", stackTrace.toString());
    }

    private void addPath(Map<String, Object> errorAttributes,
                         RequestAttributes requestAttributes) {
        String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
        if (path != null) {
            errorAttributes.put("path", path);
        }
    }
}

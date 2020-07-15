package cn.jboost.springboot.autoconfig.web;

import cn.hutool.json.JSONUtil;
import cn.jboost.springboot.common.util.ResponseWrapper;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/***
 * @Desc 统一rest接口响应格式转化
 * @Author ronwxy
 * @Date 2019/5/14 15:00   
 */
@RestControllerAdvice
public class ResponseWrapperAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        // 排除ExceptionHandler注解的方法及BasicErrorController方法（交由BaseErrorAttributes处理）
        return methodParameter.getMethodAnnotation(ExceptionHandler.class) == null
                && !methodParameter.getContainingClass().equals(BasicErrorController.class);
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        boolean isSwaggerApi = serverHttpRequest.getURI().getPath().contains("/swagger-resources")
                || serverHttpRequest.getURI().getPath().contains("/v2/api-docs");
        // 针对swagger接口或ErrorAttributes处理的响应不做转化
        if (isSwaggerApi) {
            return o;
        }
        ResponseWrapper result = ResponseWrapper.ok(o);
        //针对返回值为String类型，避免StringHttpMessageConverter调用是产生类型转换异常
        if(o instanceof String) {
            return JSONUtil.toJsonStr(result);
        }
        return result;
    }
}

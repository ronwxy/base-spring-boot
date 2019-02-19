package com.springboot.autoconfig.web;

import com.springboot.autoconfig.web.annotation.CurrentUserId;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * binding userId to {@link CurrentUserId}
 * @since 1.2
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUserId.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Exception ex = null;
        Long userId = null;
        CurrentUserId currentUserId = parameter.getParameterAnnotation(CurrentUserId.class);
        try {
            //noSuchWebParameterException
            userId = UserParamUtil.currentUserId(webRequest.getNativeRequest(HttpServletRequest.class));
        } catch (Exception e) {
            ex = e;
        }
        boolean toThrowEx = (userId == null && currentUserId.required());
        if (toThrowEx) {
            throw ex;
        }
        return userId;
    }
}

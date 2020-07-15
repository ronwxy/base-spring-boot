package cn.jboost.springboot.autoconfig.web.filter;

import cn.jboost.springboot.common.util.ResponseWrapper;
import cn.jboost.springboot.common.exception.ForbiddenException;
import cn.jboost.springboot.common.exception.UnauthorizedException;
import cn.jboost.springboot.common.util.WebUtil;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一处理Filter中抛出的异常（Filter中抛出异常无法通过 @ControllerAdvice + ExceptionHandler 处理，也不会跳转/error接口）
 */
public class ExceptionHandlerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            } else if (e instanceof ForbiddenException) {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            } else {
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            }
            ResponseWrapper errorResponse = new ResponseWrapper(httpServletResponse.getStatus(), e.getMessage());
            WebUtil.outputJson(errorResponse, (HttpServletRequest) request, httpServletResponse);
        }
    }
}
package cn.jboost.springboot.logging;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ReqIdFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest)request;

        String reqId = httpServletRequest.getHeader("Req-Id");
        if(StringUtils.isEmpty(reqId)){
            reqId = httpServletRequest.getParameter("req_id");
        }
        if(StringUtils.isEmpty(reqId)) {
            reqId = ObjectId.get().toHexString();
        }
        MDC.put("reqId", reqId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }

    }

    @Override
    public void destroy() {

    }
}

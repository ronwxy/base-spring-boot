package com.springboot.common.web;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

/**
 * use {@link RequestContextHolder} to find parameter in web environment,only support servlet stack</br>
 * the order to find is like below,if find it in one step,return immediately</br>
 * <li>header parameter:xs-p:123</li>
 * <li>url parameter:http://host:port?p=123 or post form params</li>
 * <li>cookie parameter:Cookies:xs-p=123</li>
 * <li>request attribute</li>
 * <li>default value</li>
 * <li>null or throw exception</li>
 *
 * @author liubo
 * @see RequestContextHolder
 * @see ServletRequestAttributes
 * @see WebParamObject
 * @since 1.2
 */
public final class WebParamUtil {
    /**
     * avoid the business param has the same param name as the param used internal;
     */
    public static String PARAM_PREFIX = "ax_";
    /**
     * avoid the business header has the same name as the header used internal;
     */
    public static String HEADER_COOKIE_PREFIX = "Ax-";

    /**
     * the same as the com.github.nickvl.xspring.core.log.aop.ReqIdFilter
     */
    public static String PARAM_REQ_ID = "req_id";
    public static String HEADER_COOKIE_REQ_ID = "Req-Id";

    public static HttpServletRequest currentRequest() {
        return getServletRequestAttributes()
                .getRequest();
    }


    private static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    }


    public static String findWebParam(WebParamObject webParamObject) {
        HttpServletRequest request = currentRequest();
        return findWebParam(request, webParamObject);
    }

    /**
     * @param request
     * @param webParamObject
     * @return parameter value
     * @throws NoSuchWebParameterException
     */
    public static String findWebParam(HttpServletRequest request, WebParamObject webParamObject) throws NoSuchWebParameterException {

        String paramCandidate = request.getHeader(webParamObject.getHeaderName());
        boolean tryReqParam = (paramCandidate == null && webParamObject.getReqParamName() != null);
        if (tryReqParam) {
            paramCandidate = request.getParameter(webParamObject.getReqParamName());
        }
        boolean tryCookie = (paramCandidate == null && webParamObject.getCookieName() != null);
        if (tryCookie) {
            Cookie[] cookies = request.getCookies();
            Optional<Cookie> cookie = Arrays.stream(Optional.ofNullable(cookies).orElse(new Cookie[0]))
                    .filter(c -> webParamObject.getCookieName().equals(c.getName())).findFirst();
            if (cookie.isPresent()) {
                paramCandidate = cookie.get().getValue();
            }
        }
        boolean tryAttribute = (paramCandidate == null && webParamObject.getAttributeName() != null);
        if (tryAttribute) {
            Object v = request.getAttribute(webParamObject.getAttributeName());
            if (v != null) {
                paramCandidate = v.toString();
            }
        }

        boolean tryDefault = (paramCandidate == null && webParamObject.getDefaultValue() != null);
        if (tryDefault) {
            paramCandidate = webParamObject.getDefaultValue();
        }
        boolean needThrowEx = (paramCandidate == null && webParamObject.isRequired());
        if (needThrowEx) {
            throw new NoSuchWebParameterException("can not found " + webParamObject.getReqParamName());
        }
        return paramCandidate;
    }


    /**
     * @author liubo
     */
    public static class NoSuchWebParameterException extends RuntimeException {
        public NoSuchWebParameterException() {
            super();
        }

        public NoSuchWebParameterException(String message) {
            super(message);
        }

        public NoSuchWebParameterException(String message, Throwable cause) {
            super(message, cause);
        }

        public NoSuchWebParameterException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * defined where to obtain web parameter and how to handle the {@code null} value;
     *
     * @author liubo
     * @since 1.2
     */
    public static class WebParamObject {

        /**
         * http request header parameter header name;
         * eg.xa-p=123
         */
        private final String headerName;
        /**
         * http get url parameter name or form body parameter name;<br>
         * eg.http://host:port?p=123---->p=123
         */
        private final String reqParamName;
        /**
         * http cookie parameter name;
         * eg.cookie:xa-p=123
         */
        private final String cookieName;
        /**
         * http req attribute name;
         */
        private final String attributeName;
        /**
         * if all value obtain above is {@code null},the none {@code null} defaultValue will be use;
         */
        private final String defaultValue;
        /**
         * if all value obtain above is {@code null},and required is true,throw {@link NoSuchWebParameterException},or else return {@code null}
         */
        private final boolean required;

        public WebParamObject(String headerName, String reqParamName, String cookieName, String attributeName, String defaultValue, boolean required) {
            this.headerName = headerName;
            this.reqParamName = reqParamName;
            this.cookieName = cookieName;
            this.attributeName = attributeName;
            this.defaultValue = defaultValue;
            this.required = required;
        }

        /**
         * headerName=cookieName
         * reqName=attributeName
         *
         * @param headerName
         * @param reqParamName
         */
        public WebParamObject(String headerName, String reqParamName) {
            this(headerName, reqParamName, headerName, reqParamName, null, true);
        }

        public WebParamObject(String headerName, String reqParamName, boolean required) {
            this(headerName, reqParamName, headerName, reqParamName, null, required);
        }


        /**
         * headerName=cookieName
         * reqName=attributeName
         *
         * @param headerName
         * @param reqParamName
         * @param defaultValue
         */
        public WebParamObject(String headerName, String reqParamName, String defaultValue) {
            this(headerName, reqParamName, headerName, reqParamName, defaultValue, false);
        }

        public WebParamObject(String headerName, String reqParamName, String defaultValue, boolean required) {
            this(headerName, reqParamName, headerName, reqParamName, defaultValue, required);
        }

        public String getReqParamName() {
            return reqParamName;
        }

        public String getHeaderName() {
            return headerName;
        }

        public String getCookieName() {
            return cookieName;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public boolean isRequired() {
            return required;
        }
    }
}

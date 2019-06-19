package cn.jboost.springboot.common.web;

import cn.jboost.springboot.common.web.filter.HttpResourceFilter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * if in an spring web context use the {@link RequestContextHolder},
 * if not,fall back to local {@link ThreadLocal} holder to handle {@link HttpServletRequest} and {@link HttpServletResponse}
 * must use the spring-web module to compile;
 *
 * @see org.springframework.web.context.request.RequestContextHolder
 * @see org.springframework.web.context.request.RequestContextListener
 * @see org.springframework.web.context.request.ServletRequestAttributes
 * @see HttpResourceFilter
 * this no meaning to do this,use the {@link RequestContextHolder} instead;
 */
public abstract class HttpResourceHolder {

	private static final ThreadLocal<HttpServletRequest> _requests = new ThreadLocal<>();
	private static final ThreadLocal<HttpServletResponse> _responses = new ThreadLocal<>();
	private static boolean isSpringWeb = false;

	private HttpResourceHolder() {
		try {
			Class.forName("org.springframework.web.context.request.RequestContextHolder", false, getClass().getClassLoader());
			isSpringWeb = true;
		} catch (ClassNotFoundException e) {
			//ignore;
		}
	}

	public static void setResponses(HttpServletResponse response) {
		if (!isSpringWeb) {
			_responses.set(response);
		}
	}

	public static void removeRequest() {
		if (!isSpringWeb) {
			_requests.remove();
		}
	}

	public static void removeResponse() {
		if (!isSpringWeb) {
			_responses.remove();
		}
	}

	public static HttpServletRequest getRequest(boolean throwExOnNull) {
		HttpServletRequest request;
		if (isSpringWeb) {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			request = attributes == null ? null : attributes.getRequest();
		} else {
			request = _requests.get();
		}
		if (request == null && throwExOnNull) {
			throw new IllegalStateException("Current thread is not a web thread!");
		} else {
			return request;
		}

	}

	public static HttpServletRequest getRequest() {
		return getRequest(true);
	}

	public static void setRequest(HttpServletRequest request) {
		if (!isSpringWeb) {
			_requests.set(request);
		}
	}

	public static HttpServletResponse getResponse(boolean throwExOnNull) {
		HttpServletResponse response;
		if (isSpringWeb) {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			response = attributes == null ? null : attributes.getResponse();
		} else {
			response = _responses.get();
		}
		if (response == null && throwExOnNull) {
			throw new IllegalStateException("Current thread is not a web thread!");
		} else {
			return response;
		}
	}

	public static HttpServletResponse getResponse() {
		return getResponse(true);
	}
}

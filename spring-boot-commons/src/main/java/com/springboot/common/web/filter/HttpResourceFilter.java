package com.springboot.common.web.filter;

import com.springboot.common.web.HttpResourceHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * use the spring {@link OncePerRequestFilter} to define a filter to hold {@link HttpServletRequest} and {@link HttpServletResponse}
 * this is no meaning use the {@link org.springframework.web.context.request.RequestContextHolder} to instead
 *
 */
public class HttpResourceFilter extends OncePerRequestFilter {


	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpResourceHolder.setRequest(request);
		HttpResourceHolder.setResponses(response);
		try {
			chain.doFilter(request, response);
		} finally {
			HttpResourceHolder.removeRequest();
			HttpResourceHolder.removeResponse();
		}
	}


}
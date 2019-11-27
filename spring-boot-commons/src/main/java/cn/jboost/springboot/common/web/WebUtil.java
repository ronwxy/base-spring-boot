package cn.jboost.springboot.common.web;

import cn.jboost.springboot.common.jackson.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public abstract class WebUtil {

	/**
	 * 获取ip地址
	 * @param request
	 * @return
	 */
	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return "0:0:0:0:0:0:0:1".equals(ip)?"127.0.0.1":ip;
	}

	/**
	 * determine the {@link HttpServletRequest}'s requestType is ajax or no ajax;
	 *
	 * @param httpServletRequest
	 * @return
	 * @see WebRequestType
	 */
	public static WebRequestType requestType(HttpServletRequest httpServletRequest) {
		String header = httpServletRequest.getHeader("X-Requested-With");
		return "XMLHttpRequest".equalsIgnoreCase(header) ? WebRequestType.ajax : WebRequestType.no_ajax;
	}

	public static WebRequestType requestType() {
		HttpServletRequest httpServletRequest = HttpResourceHolder.getRequest();
		return requestType(httpServletRequest);
	}

	public static boolean isAjax(HttpServletRequest httpServletRequest) {
		return requestType(httpServletRequest) == WebRequestType.ajax;
	}

	public static boolean isAjax() {
		return isAjax(HttpResourceHolder.getRequest());
	}

	public static boolean isIE(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0;
	}

	public static boolean isIE() {
		return isIE(HttpResourceHolder.getRequest());
	}

	public static void outputJson(Object object, boolean success) {
		outputJson(object, success, HttpResourceHolder.getRequest(), HttpResourceHolder.getResponse());
	}

	public static void outputJson(Object object, boolean success, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<>();
		result.put("data", object);
		result.put("success", success);
		outputJson(result, request, response);
	}

	public static void outputJson(Object object) {
		outputJson(object, HttpResourceHolder.getRequest(), HttpResourceHolder.getResponse());
	}

	public static void outputJson(Object object, HttpServletRequest request, HttpServletResponse response) {
		String contentType = "application/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setHeader("Pragma", "no-cache");

		try (Writer writer = response.getWriter()) {
			JsonUtil.toJson(object, writer);
			writer.flush();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void outputText(Object object) {
		outputText(object, HttpResourceHolder.getRequest(), HttpResourceHolder.getResponse());
	}

	public static void outputText(Object object, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/plain;charset=" + StandardCharsets.UTF_8.name());
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setHeader("Pragma", "no-cache");

		try (OutputStream out = response.getOutputStream()) {
			String text = object instanceof String ? (String) object : object.toString();
			IOUtils.write(text, out, StandardCharsets.UTF_8);
			out.flush();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void outputBinary(String originalFilename, InputStream in, String contentType) throws IOException {
		outputBinary(originalFilename, in, contentType, HttpResourceHolder.getRequest(), HttpResourceHolder.getResponse());
	}

	public static void outputBinary(String originalFilename, InputStream in, String contentType, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String agent = request.getHeader("USER-AGENT");
		String filename;
		if (agent.contains("MSIE") || agent.contains("Trident")) {
			filename = URLEncoder.encode(originalFilename, "UTF-8");
		} else { // specialized for ie 11 below
			filename = new String(originalFilename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		}

		if (!StringUtils.isEmpty(filename)) {
			response.setHeader("Content-disposition", "attachment; filename=" + filename);
		} else {
			response.setHeader("Content-Disposition", "inline");
		}
		response.setContentType(contentType);
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setHeader("Pragma", "no-cache");
		try (OutputStream out = response.getOutputStream(); InputStream inputStream = in) {
			IOUtils.copy(inputStream, out);
			out.flush();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * web request type
	 */
	public enum WebRequestType {
		ajax, no_ajax
	}
}

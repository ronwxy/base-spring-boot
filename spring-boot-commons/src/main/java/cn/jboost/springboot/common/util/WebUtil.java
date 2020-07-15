package cn.jboost.springboot.common.util;

import cn.hutool.core.lang.ObjectId;
import cn.hutool.json.JSONUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

	public static final String REQ_ID_HEADER = "Req-Id";
	private static final String UNKNOWN = "unknown";

	private static final ThreadLocal<String> reqIdThreadLocal = new ThreadLocal<>();

	public static void setRequestId(String requestId) {
		reqIdThreadLocal.set(requestId);
	}

	public static String getRequestId(){
		String requestId = reqIdThreadLocal.get();
		if(requestId == null) {
			requestId = ObjectId.next();
			reqIdThreadLocal.set(requestId);
		}
		return requestId;
	}

	public static void removeRequestId() {
		reqIdThreadLocal.remove();
	}

	/**
	 * 获取ip地址
	 * @return
	 */
	public static String getIP() {
		HttpServletRequest request = getRequest();
		String ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return "0:0:0:0:0:0:0:1".equals(ip)?"127.0.0.1":ip;
	}

	public static void outputJson(Object object, boolean success, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<>();
		result.put("data", object);
		result.put("success", success);
		outputJson(result, request, response);
	}

	public static void outputJson(Object object) {
		outputJson(object, getRequest(), getResponse());
	}

	public static void outputJson(Object object, HttpServletRequest request, HttpServletResponse response) {
		String contentType = "application/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setHeader("Pragma", "no-cache");

		try (Writer writer = response.getWriter()) {
			JSONUtil.toJsonStr(object, writer);
			writer.flush();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void outputText(Object object) {
		outputText(object, getRequest(), getResponse());
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
		outputBinary(originalFilename, in, contentType, getRequest(), getResponse());
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

	public static HttpServletRequest getRequest() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		return requestAttributes.getRequest();
	}

	public static HttpServletResponse getResponse() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		return requestAttributes.getResponse();
	}
}

package com.springboot.boot.error.feign.decoder;

import com.springboot.boot.error.exception.BizException;
import com.springboot.common.jackson.JsonUtil;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jsuser on 2018/5/4.
 */
public class DefaultErrorDecoder implements ErrorDecoder {

	final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		try {
			if (response.body() != null) {
				String body = Util.toString(response.body().asReader());
				@SuppressWarnings("unchecked")
				Map<String, Object> map = JsonUtil.fromJson(body, Map.class);

				return new BizException(
						response.status(), MapUtils.getString(map, BizException.ERROR_CODE),
						MapUtils.getString(map, BizException.ERROR_MESSAGE)
				);
			}
		} catch (IOException fallbackToDefault) {
			return defaultDecoder.decode(methodKey, response);
		}
		return null;
	}
}
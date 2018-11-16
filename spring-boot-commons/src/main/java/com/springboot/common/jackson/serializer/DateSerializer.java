package com.springboot.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.springboot.common.time.DateTimeUtil;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {

	private final static DateSerializer _instance = new DateSerializer();

	private DateSerializer() {

	}

	public static DateSerializer getInstance() {
		return _instance;
	}

	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		String dateString;
		if (!DateTimeUtil.isExistTimePart(value)) {
			dateString = DateTimeUtil.formatAsYYYYMMdd(value);
		} else {
			dateString = DateTimeUtil.formatAsYYYYMMddHHmmss(value);
		}

		jgen.writeString(dateString);
	}
}
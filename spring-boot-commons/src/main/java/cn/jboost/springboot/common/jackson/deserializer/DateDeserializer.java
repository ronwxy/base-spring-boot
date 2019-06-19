package cn.jboost.springboot.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import cn.jboost.springboot.common.time.DateTimeUtil;

import java.io.IOException;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {

	private final static DateDeserializer _instance = new DateDeserializer();

	private DateDeserializer() {
	}

	public static DateDeserializer getInstance() {
		return _instance;
	}

	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		String dateString = jp.getText();
		Date date;
		switch (dateString.length()) {
			case 10:
				date = DateTimeUtil.parseAsYYYYMMdd(dateString);
				break;
			case 19:
				date = DateTimeUtil.parseAsYYYYMMddHHmmss(dateString);
				break;
			default:
				date = null;
				break;
		}
		if (date == null)
			throw new IllegalArgumentException("illegal date format:" + dateString);
		else
			return date;
	}
}
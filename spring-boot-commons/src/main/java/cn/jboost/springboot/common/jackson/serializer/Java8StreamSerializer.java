package cn.jboost.springboot.common.jackson.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

public class Java8StreamSerializer extends StdSerializer<Stream> {

	private final static Java8StreamSerializer _instance = new Java8StreamSerializer();

	private Java8StreamSerializer() {
		super(Stream.class, true);
	}

	public static Java8StreamSerializer getInstance() {
		return _instance;
	}

	@Override
	public void serialize(Stream stream, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		provider.findValueSerializer(Iterator.class, null).serialize(stream.iterator(), jgen, provider);
	}
}

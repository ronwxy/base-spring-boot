package com.springboot.common.jackson;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.springboot.common.jackson.deserializer.DateDeserializer;
import com.springboot.common.jackson.serializer.DateSerializer;
import com.springboot.common.jackson.serializer.Java8StreamSerializer;
import org.springframework.beans.factory.FactoryBean;

import java.util.Date;
import java.util.stream.Stream;

public class ObjectMapperFactory implements FactoryBean<ObjectMapper> {


	private static final ObjectMapper _jsonMapper = new ObjectMapper();

	static {
		addModules(_jsonMapper);
		_jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static ObjectMapper get() {
		return _jsonMapper;
	}

	public static void addModules(ObjectMapper objectMapper) {
		SimpleModule module = new SimpleModule();
		module.addSerializer(Date.class, DateSerializer.getInstance());
		module.addDeserializer(Date.class, DateDeserializer.getInstance());
		module.addSerializer(Stream.class, Java8StreamSerializer.getInstance());
		objectMapper.registerModule(module);

	}

	@Override
	public ObjectMapper getObject() {
		return _jsonMapper;
	}

	@Override
	public Class<?> getObjectType() {
		return ObjectMapper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}

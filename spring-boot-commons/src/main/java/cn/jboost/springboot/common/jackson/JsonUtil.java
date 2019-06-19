package cn.jboost.springboot.common.jackson;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;


public abstract class JsonUtil {

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    public static <T> byte[] toJsonByteArray(T bean) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
        toJson(bean, baos);
        return baos.toByteArray();
    }

    public static <T> String toJson(T bean) {
        StringWriter writer = new StringWriter();
        toJson(bean, writer);
        return writer.toString();
    }

    public static <T> void toJson(T bean, OutputStream outputStream) {
        toJson(bean, new OutputStreamWriter(outputStream, DEFAULT_CHARSET));
    }

    public static <T> void toJson(T bean, Writer writer) {
        try {
            JsonGenerator jsonGenerator = ObjectMapperFactory.get().getFactory().createGenerator(writer);
            ObjectMapperFactory.get().writeValue(jsonGenerator, bean);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> valueType) {
        return fromJson(new StringReader(json), valueType);
    }

    public static <T> T fromJson(InputStream inputStream, Class<T> valueType) {
        return fromJson(new InputStreamReader(inputStream, DEFAULT_CHARSET), valueType);
    }

    public static <T> T fromJson(byte[] bytes, Class<T> valueType) {
        return fromJson(new InputStreamReader(new ByteArrayInputStream(bytes), DEFAULT_CHARSET), valueType);
    }

    public static <T> T fromJson(Reader reader, Class<T> valueType) {
        try {
            return ObjectMapperFactory.get().readValue(reader, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(InputStream inputStream, TypeReference<T> typeReference) {
        return fromJson(new InputStreamReader(inputStream, DEFAULT_CHARSET), typeReference);
    }

    public static <T> T fromJson(String s, TypeReference<T> typeReference) {
        return fromJson(new StringReader(s), typeReference);
    }

    public static <T> T fromJson(byte[] bytes, TypeReference<T> typeReference) {
        return fromJson(new InputStreamReader(new ByteArrayInputStream(bytes), DEFAULT_CHARSET), typeReference);
    }

    public static <T> T fromJson(Reader reader, TypeReference<T> typeReference) {
        try {
            return ObjectMapperFactory.get().readValue(reader, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseMap(String json) {
        return (Map<String, Object>) fromJson(json, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> parseArray(String jsonString) {
        return fromJson(jsonString, new TypeReference<List<Object>>() {
        });
    }

    public static <T> List<T> parseArray(String jsonString, Class<?> clazz) {
        try {
            return ObjectMapperFactory.get().readValue(jsonString, ObjectMapperFactory.get().getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object parseJson(String jsonString) {
        if (jsonString != null && !"".equals(jsonString)) {
            if (jsonString.startsWith("{")) {
                return parseMap(jsonString);
            } else if (jsonString.startsWith("[")) {
                return parseArray(jsonString);
            } else {
                return null;
            }
        }
        return null;
    }


}

package com.springboot.boot.alimns.utils;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static Map fromJsonAsMap(String json) {
        return gson.fromJson(json, new TypeToken<Map>(){}.getType());
    }

}

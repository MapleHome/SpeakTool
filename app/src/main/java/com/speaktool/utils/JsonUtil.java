package com.speaktool.utils;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

public class JsonUtil {

	public static String toJson(Object obj) {
		if (obj == null)
			return null;
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	public static <T> T fromJon(String json, Class<T> classOfT) {
		if (json == null || json.length() == 0)
			return null;
		final Gson gson = new Gson();
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJonGeneric(String json, Type typeOfT) {
		if (json == null || json.length() == 0)
			return null;
		final Gson gson = new Gson();
		return gson.fromJson(json, typeOfT);
		// Type collectionType2 = new TypeToken<List<String>>() {
		// }.getType();
	}

	public static <T> T jsonToList(String json, TypeToken<T> typeToken) {
		if (json == null || json.length() == 0)
			return null;
		final Gson gson = new Gson();
		return gson.fromJson(json, typeToken.getType());
	}
}

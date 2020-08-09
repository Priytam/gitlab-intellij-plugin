package com.intellij.gitlab.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collections;
import java.util.List;

import static com.intellij.openapi.util.text.StringUtil.trim;
import static java.util.Objects.nonNull;

public class GitLabGsonUtil {

    public static JsonObject createNameObject(String value){
        return createObject("name", value);
    }

    public static JsonArray createArrayNameObject(String value){
        return createArrayNameObjects(Collections.singletonList(value));
    }

    public static JsonArray createArrayNameObjects(List<String> values){
        JsonArray array = new JsonArray();

        for(String value : values){
            JsonObject name = new JsonObject();
            name.add("name", createPrimitive(value));
            array.add(name);
        }

        return array;
    }

    public static JsonElement createNameObject(String value, boolean isArray){
        return isArray ? createArrayNameObject(value) : createNameObject(value);
    }


    public static JsonObject createObject(String property, String value){
        JsonObject name = new JsonObject();
        name.add(property, createPrimitive(value));

        return name;
    }

    public static boolean isEmpty(JsonArray jsonArray){
        return nonNull(jsonArray) && jsonArray.size() == 0;
    }

    public static JsonElement createPrimitive(String value){
        return new JsonPrimitive(trim(value));
    }

    public static JsonElement createPrimitive(Double value){
        return new JsonPrimitive(value);
    }

    public static JsonElement createPrimitive(Integer value){
        return new JsonPrimitive(value);
    }
}

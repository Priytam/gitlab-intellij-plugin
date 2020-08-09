package com.intellij.gitlab.util.result;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

public class EmptyResult implements Result {

    private String response;

    private EmptyResult(@Nullable String response) {
        this.response = response;
    }


    public static EmptyResult create(String response){
        return new EmptyResult(response);
    }

    public static EmptyResult error(){
        return new EmptyResult("null");
    }

    @Override
    public boolean isValid() {
        return StringUtil.isEmpty(response);
    }

    @Override
    public Object get() {
        return null;
    }
}

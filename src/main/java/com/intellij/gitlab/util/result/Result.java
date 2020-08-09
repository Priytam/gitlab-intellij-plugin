package com.intellij.gitlab.util.result;

public interface Result<T> {

    boolean isValid();


    T get();

}

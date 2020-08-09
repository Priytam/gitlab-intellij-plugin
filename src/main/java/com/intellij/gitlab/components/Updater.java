package com.intellij.gitlab.components;

import java.util.List;

public interface Updater<T> {

    void update(List<T> t);

    void update(T t);

}

package com.intellij.gitlab.ui.editors;

import com.google.gson.JsonElement;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public interface FieldEditor {

     JComponent createPanel();

     JsonElement getJsonValue();

     boolean isRequired();

     @Nullable
     ValidationInfo validate();

}

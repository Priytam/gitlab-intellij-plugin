package com.intellij.gitlab.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import static com.intellij.gitlab.util.GitLabGsonUtil.createPrimitive;
import static com.intellij.openapi.util.text.StringUtil.*;

public class NumberFieldEditor extends TextFieldEditor {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+([,.]\\d{1,2})?");

    public NumberFieldEditor(String issueKey, String fieldName, String fieldValue, boolean required) {
        super(issueKey, fieldName, fieldValue, required);
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        return createPrimitive(Double.valueOf(myTextField.getText()));
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(trim(myTextField.getText()))){
            return new ValidationInfo(myLabel.getMyLabelText() + " is required");
        }

        if(isNotEmpty(trim(myTextField.getText()))){
            if(!NUMBER_PATTERN.matcher(trim(myTextField.getText())).matches()){
                return new ValidationInfo("Wrong format in " + myLabel.getMyLabelText() + " field.");
            }
        }

        return null;
    }

}

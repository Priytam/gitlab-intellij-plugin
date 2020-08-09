package com.intellij.gitlab.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.awt.*;
import java.util.Objects;

import static com.intellij.gitlab.util.GitLabGsonUtil.createPrimitive;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;

public class TextFieldEditor extends AbstractFieldEditor<String> {

    protected JBTextField myTextField;

    public TextFieldEditor(String issueKey, String fieldName, Object fieldValue, boolean required) {
        super(issueKey, fieldName, fieldValue, required);
    }

    @Override
    public String getFieldValue() {
        return Objects.nonNull(fieldValue) ? (String) fieldValue : "";
    }

    @Override
    public JComponent createPanel() {
        this.myTextField = new JBTextField();
        this.myTextField.setPreferredSize(getFieldSize());
        this.myTextField.setText(getFieldValue());

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(this.myLabel, this.myTextField)
                .getPanel();
    }

    public Dimension getFieldSize() {
        return UI.size(250, 24);
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        return createPrimitive(myTextField.getText());
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(trim(myTextField.getText()))){
            return new ValidationInfo(myLabel.getMyLabelText() + " is required");
        }

        return null;
    }

    public JBTextField getMyTextField() {
        return myTextField;
    }
}

package com.intellij.gitlab.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.gitlab.util.GitLabLabelUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class LabelFieldEditor extends AbstractFieldEditor<String> {

    private JBLabel myLabelText;

    public LabelFieldEditor(String issueKey, String fieldName) {
        this(issueKey, fieldName, null);
    }

    public LabelFieldEditor(String issueKey, String fieldName, Object fieldValue) {
        super(issueKey, fieldName, fieldValue);
    }

    @Override
    public JComponent createPanel() {
        this.myLabelText = GitLabLabelUtil.createBoldLabel(getFieldValue());

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(this.myLabel, this.myLabelText)
                .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        return JsonNull.INSTANCE;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        return null;
    }

    @Override
    public String getFieldValue() {
        return Objects.nonNull(fieldValue) ? (String) fieldValue : "None";
    }

}

package com.intellij.gitlab.ui.editors;

import com.intellij.ui.components.JBLabel;

public abstract class AbstractFieldEditor<T> implements FieldEditor {

    protected String issueKey;
    protected MyLabel myLabel;
    protected Object fieldValue;
    private boolean required;

    public AbstractFieldEditor(String issueKey, String fieldName, Object fieldValue) {
        this(issueKey, fieldName, fieldValue, false);
    }

    public AbstractFieldEditor(String issueKey, String fieldName, Object fieldValue, boolean required) {
        this.issueKey = issueKey;
        this.myLabel = new MyLabel(fieldName, required);
        this.fieldValue = fieldValue;
        this.required = required;
    }

    public abstract T getFieldValue();

    @Override
    public boolean isRequired() {
        return required;
    }

    class MyLabel extends JBLabel{
        private String myLabelText;

        public MyLabel(String myLabelText, boolean required) {
            super();
            this.myLabelText = myLabelText;
            setText(myLabelText + (required ? " *" : ""));
        }


        public String getMyLabelText() {
            return myLabelText;
        }

    }

}

package com.intellij.gitlab.ui.editors;

import com.google.gson.JsonElement;
import com.intellij.gitlab.util.GitLabGsonUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.util.text.StringUtil.*;

public class TimeSpentEditor extends TextFieldEditor {

    private static final Pattern TIME_SPENT_SIMPLE_PATTERN = Pattern.compile("(\\d+)([wdhm])");
    private static final Pattern TIME_SPENT_MULTI_PATTERN = Pattern.compile("(\\d+[wdhm])(\\s{1}\\d+[wdhm])*");

    public TimeSpentEditor(String issueKey) {
        this(issueKey, "Time Spent", null, false);
    }

    public TimeSpentEditor(String issueKey, Object fieldValue, boolean required) {
        this(issueKey, "Time Spent", fieldValue, required);
    }

    public TimeSpentEditor(String issueKey, String fieldName, Object fieldValue, boolean required) {
        super(issueKey, fieldName, fieldValue, required);
    }

    @Override
    public Dimension getFieldSize() {
        return UI.size(150, 24);
    }

    @Override
    public JsonElement getJsonValue() {
        int timeSpentInSeconds = 0;
        for(String ts : myTextField.getText().split(" ")){
            Matcher matcher = TIME_SPENT_SIMPLE_PATTERN.matcher(ts);
            if(matcher.find()){
                Integer number = Integer.parseInt(matcher.group(1));
                String letter = matcher.group(2);

                switch (letter){
                    case "w": timeSpentInSeconds += number * 144000; break;
                    case "d": timeSpentInSeconds += number * 28800; break;
                    case "h": timeSpentInSeconds += number * 3600; break;
                    case "m": timeSpentInSeconds += number * 60; break;
                }
            }
        }

        return GitLabGsonUtil.createPrimitive(timeSpentInSeconds);
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        ValidationInfo info = super.validate();
        if(Objects.isNull(info)){
            if(isNotEmpty(trim(myTextField.getText()))) {
                String timeSpent = myTextField.getText();
                if(!TIME_SPENT_MULTI_PATTERN.matcher(timeSpent).matches()){
                    return new ValidationInfo("Invalid time duration entered", myTextField);
                }
            }
        }

        return null;
    }
}

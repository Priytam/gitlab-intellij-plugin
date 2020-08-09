package com.intellij.gitlab.ui.editors;

import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.intellij.openapi.util.text.StringUtil.*;

public class DateTimeFieldEditor extends DateFieldEditor {

    private static final DateFormatter DATE_TIME_FORMATTER = new DateFormatter(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.ss+0000";

    public DateTimeFieldEditor(String issueKey, String fieldName, boolean required) {
        super(issueKey, fieldName, null, required);
    }

    public DateTimeFieldEditor(String issueKey, String fieldName, Object fieldValue, boolean required) {
        super(issueKey, fieldName, fieldValue, required);
    }

    @Override
    public String getToolTipMessage() {
        return "E.g. yyyy-MM-dd HH:mm";
    }

    @Override
    protected String getValue() {
        String dateTime = super.getValue();
        String[] words = dateTime.split(" ");
        if(words.length != 2){
            return "";
        }

        LocalDate ld = LocalDate.parse(words[0]);
        LocalTime lt = LocalTime.parse(words[1]);

        return DateTimeFormatter.ofPattern(ISO_FORMAT).format(LocalDateTime.of(ld, lt));
    }

    @Override
    public DateFormatter getDateFormatter() {
        return DATE_TIME_FORMATTER;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(trim(myFormattedTextField.getText()))){
            return new ValidationInfo(myLabel.getMyLabelText() + " is required.");
        }else{
            if(isNotEmpty(trim(myFormattedTextField.getText()))){
                try{
                    LocalDateTime.parse(myFormattedTextField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                }catch (DateTimeParseException e){
                    return new ValidationInfo("Wrong format in " + myLabel.getMyLabelText() + " field.");
                }
            }

        }

        return null;
    }

}

package com.intellij.gitlab.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.List;

public class VisibilityFieldEditor extends ComboBoxFieldEditor<String> {

    public VisibilityFieldEditor(String issueKey, Object fieldValue, List<String> items) {
        super(issueKey, "Viewable by", fieldValue, false, items);
    }

    @Override
    public JsonElement getJsonValue() {
        String selectedValue = getSelectedValue();
        if("All Users".equals(selectedValue)){
            return JsonNull.INSTANCE;
        }

        JsonObject visibility = new JsonObject();
        visibility.addProperty("type", "role");
        visibility.addProperty("value", selectedValue);

        return visibility;
    }

}

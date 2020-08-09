package com.intellij.gitlab.actions;

import com.intellij.openapi.actionSystem.CustomShortcutSet;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static java.util.Objects.isNull;

public class ActionProperties {

    private String text;
    private String description;
    private Icon icon;
    private CustomShortcutSet shortcut;

    public static ActionProperties of(String text){
        return new ActionProperties(text, null, null, null);
    }

    public static ActionProperties of(String text, Icon icon){
        return new ActionProperties(text, null, icon, null);
    }

    public static ActionProperties of(String text, String description, Icon icon){
        return new ActionProperties(text, description, icon, null);
    }

    public static ActionProperties of(String text, Icon icon, String shortcut){
        return new ActionProperties(text, null, icon, shortcut);
    }

    private ActionProperties(@Nullable String text, @Nullable String description, @Nullable Icon icon, @Nullable String shortcut) {
        this.text = text;
        this.description = description;
        this.icon = icon;
        this.shortcut = isNull(shortcut) ? CustomShortcutSet.EMPTY : CustomShortcutSet.fromString(shortcut);
    }


    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    public Icon getIcon() {
        return icon;
    }

    public CustomShortcutSet getShortcut() {
        return shortcut;
    }
}

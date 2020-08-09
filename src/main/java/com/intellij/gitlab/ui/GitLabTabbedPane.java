package com.intellij.gitlab.ui;

import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class GitLabTabbedPane extends JBTabbedPane {


    public GitLabTabbedPane(int tabPlacement) {
        super(tabPlacement);
    }


    @NotNull
    @Override
    protected Insets getInsetsForTabComponent() {
        return JBUI.insets(0);
    }

}

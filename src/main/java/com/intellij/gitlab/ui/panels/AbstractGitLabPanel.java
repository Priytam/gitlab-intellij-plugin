package com.intellij.gitlab.ui.panels;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

import javax.swing.*;

import static com.intellij.gitlab.ui.GitLabToolWindowFactory.TOOL_WINDOW_ID;

public abstract class AbstractGitLabPanel<T> extends SimpleToolWindowPanel {

    public AbstractGitLabPanel(T object) {
        this(false, object);
    }

    public AbstractGitLabPanel(boolean borderless, T pipeline) {
        this(true, borderless, pipeline);
    }

    public AbstractGitLabPanel(boolean vertical, boolean borderless, T pipeline) {
        super(vertical, borderless);
        initToolbar();
    }

    public void initToolbar(){
        ActionToolbar actionToolbar = getActionToolbar();
        actionToolbar.setTargetComponent(this);

        Box toolBarBox = getToolBarBox();
        toolBarBox.add(actionToolbar.getComponent());
        setToolbar(toolBarBox);
    }

    public ActionToolbar getActionToolbar(){
        return ActionManager.getInstance().createActionToolbar(TOOL_WINDOW_ID, getActionGroup(), true);
    }

    public Box getToolBarBox(){
        return Box.createHorizontalBox();
    }

    public abstract ActionGroup getActionGroup();

}

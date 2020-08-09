package com.intellij.gitlab.actions;

import com.intellij.openapi.actionSystem.DefaultActionGroup;

import javax.swing.*;

public class GitLabPipelineActionGroup extends DefaultActionGroup {

    private JComponent parent;

    public GitLabPipelineActionGroup(JComponent component) {
        super();
        this.parent = component;
    }

    public void add(GitLabPipelineAction action){
        action.registerComponent(parent);
        super.add(action);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}

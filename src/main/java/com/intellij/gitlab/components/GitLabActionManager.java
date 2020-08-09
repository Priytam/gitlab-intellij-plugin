package com.intellij.gitlab.components;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;
import org.jetbrains.annotations.NotNull;

public class GitLabActionManager implements BaseComponent {

    private AnAction gitLabPipelineRefreshAction;


    @Override
    public void initComponent() {
        gitLabPipelineRefreshAction = ActionManager.getInstance().getAction("Gitlab.toolwindow.Refresh");
    }

    @Override
    public void disposeComponent() {
        //do nothing
    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    public static GitLabActionManager getInstance() {
        return ApplicationManager.getApplication().getComponent(GitLabActionManager.class);
    }

    public AnAction getGitLabPipelineRefreshAction() {
        return gitLabPipelineRefreshAction;
    }

}

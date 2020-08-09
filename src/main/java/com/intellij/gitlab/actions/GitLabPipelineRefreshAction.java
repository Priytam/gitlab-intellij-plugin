package com.intellij.gitlab.actions;

import com.intellij.gitlab.server.GitLabServerManager;
import com.intellij.gitlab.tasks.RefreshPipelineTask;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class GitLabPipelineRefreshAction extends AnAction {


    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        if (isNull(project) || !project.isInitialized() || project.isDisposed()) {
            event.getPresentation().setEnabled(false);
        } else {
            GitLabServerManager manager = project.getComponent(GitLabServerManager.class);
            if (manager.hasGitLabServerConfigured()) {
                event.getPresentation().setEnabled(true);
            } else {
                event.getPresentation().setEnabled(false);
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (nonNull(project)) {
            new RefreshPipelineTask(project).queue();
        }
    }

}

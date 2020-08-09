package com.intellij.gitlab.tasks;

import com.intellij.gitlab.components.GitLabNotificationManager;
import com.intellij.gitlab.components.GitLabPipelineUpdater;
import com.intellij.gitlab.exceptions.GitLabServerConfigurationNotFoundException;
import com.intellij.gitlab.server.GitLabServerManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.gitlab4j.api.GitLabApi;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class AbstractBackgroundableTask extends Task.Backgroundable {

    public AbstractBackgroundableTask(@NotNull Project project, @NotNull String title) {
        super(project, title, false, ALWAYS_BACKGROUND);
    }

    @NotNull
    public GitLabApi getGitLabRestApi() throws GitLabServerConfigurationNotFoundException {
        GitLabServerManager gitLabServerManager = myProject.getComponent(GitLabServerManager.class);
        GitLabApi gitLabApi = gitLabServerManager.getGitLabApi();
        if(isNull(gitLabApi)) {
            throw new GitLabServerConfigurationNotFoundException();
        }

        return gitLabApi;
    }

    public GitLabPipelineUpdater getGitLabPipelineUpdater(){
        return myProject.getComponent(GitLabPipelineUpdater.class);
    }

    public void showNotification(String title, String content){
        Notifications.Bus.notify(GitLabNotificationManager.getInstance().createNotification(title, content));
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        String content = nonNull(error.getCause()) ? error.getCause().getMessage() : "";
        Notifications.Bus.notify(GitLabNotificationManager.getInstance().createNotificationError(error.getMessage(), content));
    }

}

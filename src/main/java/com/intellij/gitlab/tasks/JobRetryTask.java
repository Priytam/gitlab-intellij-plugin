package com.intellij.gitlab.tasks;

import com.intellij.gitlab.components.GitLabNotificationManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Pipeline;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JobRetryTask extends AbstractBackgroundableTask {

    private Integer jobId;
    private org.gitlab4j.api.models.Project selectedProject;

    public JobRetryTask(@NotNull Project project, Integer jobId, org.gitlab4j.api.models.Project selectedProject) {
        super(project, "Retrying Job " + jobId + " ...");
        this.jobId = jobId;
        this.selectedProject = selectedProject;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        GitLabApi gitLabApi = getGitLabRestApi();

        try {
            gitLabApi.getJobApi().retryJob(selectedProject, jobId);
        } catch (GitLabApiException e) {
           throw new RuntimeException(e);
        }
        try {
            List<Pipeline> pipelines = gitLabApi.getPipelineApi().getPipelines(selectedProject);
            getGitLabPipelineUpdater().update(pipelines);
        } catch (Exception e) {
            showNotification("GitLab", "Unable to refresh");
        }
    }

    @Override
    public void showNotification(String title, String content) {
        Notifications.Bus.notify(GitLabNotificationManager.getInstance().createSilentNotification(title, content));
    }

    @Override
    public void onSuccess() {
        showNotification("GitLab", "Retry submitted");
    }

}

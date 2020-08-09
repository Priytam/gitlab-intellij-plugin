package com.intellij.gitlab.tasks;

import com.intellij.gitlab.components.GitLabNotificationManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.openapi.vcs.impl.VcsInitObject;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitVcs;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.gitlab4j.api.models.Pipeline;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RefreshPipelineTask extends AbstractBackgroundableTask {

    private String gitProjectName;

    public RefreshPipelineTask(@NotNull Project project) {
        super(project, "Updating pipelines from Server");
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        getVcsProjectAndRefresh();
    }

    @Override
    public void showNotification(String title, String content) {
        Notifications.Bus.notify(GitLabNotificationManager.getInstance().createSilentNotification(title, content));
    }

    @Override
    public void onSuccess() {
        showNotification("GitLab", "Pipelines are now up to date");
    }

    private void getVcsProjectAndRefresh() {
        ProjectLevelVcsManagerImpl vcsManager = (ProjectLevelVcsManagerImpl) ServiceManager.getService(myProject, ProjectLevelVcsManager.class);
        GitRepositoryManager gitRepoManager = ServiceManager.getService(myProject, GitRepositoryManager.class);;
        vcsManager.addInitializationRequest(VcsInitObject.AFTER_COMMON, () -> {
            VirtualFile[] gitRoots = vcsManager.getRootsUnderVcs(GitVcs.getInstance(myProject));
            for (VirtualFile root : gitRoots) {
                GitRepository repo = gitRepoManager.getRepositoryForRoot(root);
                if (repo != null) {
                     gitProjectName = repo.getProject().getName();
                }
            }
            List<Pipeline> pipelines;
            try {
                org.gitlab4j.api.models.Project project = getGitLabRestApi().getProjectApi().getProjects(gitProjectName).get(0);
                pipelines = getGitLabRestApi().getPipelineApi().getPipelines(project);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            getGitLabPipelineUpdater().update(pipelines);
        });
    }
}

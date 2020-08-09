package com.intellij.gitlab.actions;

import com.intellij.gitlab.tasks.AbstractBackgroundableTask;
import com.intellij.gitlab.util.factory.GitLabPipelineFactory;
import com.intellij.gitlab.util.factory.GitLabSelectedProjectFactory;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.gitlab4j.api.models.Pipeline;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

public class GitLabPipelineTaskAction extends GitLabPipelineAction {

    private final GitLabSelectedProjectFactory selectedProjectFactory;
    private final PipelineTask task;
    private final GitLabPipelineFactory pipelineFactory;

    public static final Map<PipelineTask, Icon> iconMap = new HashMap<PipelineTask, Icon>() {
        {
            put(PipelineTask.CANCEL_TASK, AllIcons.Actions.Cancel);
            put(PipelineTask.RETRY_TASK, AllIcons.Actions.Restart);
            put(PipelineTask.DELETE_PIPELINE, AllIcons.General.Remove);

        }
    };
    public enum PipelineTask {
        CANCEL_TASK("Cancel pipeline"), RETRY_TASK("Retry pipeline"), DELETE_PIPELINE("Delete pipeline");

        private final String name;

        PipelineTask(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public GitLabPipelineTaskAction(PipelineTask task, GitLabSelectedProjectFactory selectedProjectFactory, GitLabPipelineFactory pipelineFactory) {
        super(ActionProperties.of(task.getName(), iconMap.get(task)));
        this.selectedProjectFactory = selectedProjectFactory;
        this.pipelineFactory = pipelineFactory;
        this.task = task;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (nonNull(project)) {
            switch (task) {
                case RETRY_TASK:
                    new AbstractBackgroundableTask(project, "Pipeline Retry") {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            try {
                                getGitLabRestApi().getPipelineApi().retryPipelineJob(selectedProjectFactory.create(), pipelineFactory.create().getId());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                List<Pipeline> pipelines = getGitLabRestApi().getPipelineApi().getPipelines(selectedProjectFactory.create());
                                getGitLabPipelineUpdater().update(pipelines);
                            } catch (Exception e) {
                                showNotification("GitLab", "Unable to refresh");
                            }
                        }

                        @Override
                        public void onSuccess() {
                            showNotification("GitLab", "Retry submitted");
                        }

                    }.queue();
                    break;
                case CANCEL_TASK:
                    new AbstractBackgroundableTask(project, "Pipeline Cancel") {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            try {
                                getGitLabRestApi().getPipelineApi().cancelPipelineJobs(selectedProjectFactory.create(), pipelineFactory.create().getId());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                List<Pipeline> pipelines = getGitLabRestApi().getPipelineApi().getPipelines(selectedProjectFactory.create());
                                getGitLabPipelineUpdater().update(pipelines);
                            } catch (Exception e) {
                                showNotification("GitLab", "Unable to refresh");
                            }
                        }

                        @Override
                        public void onSuccess() {
                            showNotification("GitLab", "Cancel done");
                        }
                    }.queue();
                    break;
                case DELETE_PIPELINE:
                    new AbstractBackgroundableTask(project, "Pipeline delete") {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            try {
                                getGitLabRestApi().getPipelineApi().cancelPipelineJobs(selectedProjectFactory.create(), pipelineFactory.create().getId());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            try {
                                List<Pipeline> pipelines = getGitLabRestApi().getPipelineApi().getPipelines(selectedProjectFactory.create());
                                getGitLabPipelineUpdater().update(pipelines);
                            } catch (Exception e) {
                                showNotification("GitLab", "Unable to refresh");
                            }
                        }

                        @Override
                        public void onSuccess() {
                            showNotification("GitLab", "Delete done");
                        }
                    }.queue();
                    break;
            }
        }


    }

}

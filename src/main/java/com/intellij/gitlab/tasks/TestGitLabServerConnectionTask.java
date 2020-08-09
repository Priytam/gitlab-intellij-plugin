package com.intellij.gitlab.tasks;

import com.intellij.gitlab.server.GitLabServer;
import com.intellij.gitlab.server.GitLabServerManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestGitLabServerConnectionTask extends Task.Modal {

    private Project project;
    private Exception exception;
    private TaskRepository.CancellableConnection connection;
    private GitLabServer gitLabServer;

    public TestGitLabServerConnectionTask(@Nullable Project project, @NotNull GitLabServer server) {
        super(project, "Test Connection", true);
        this.project = project;
        this.gitLabServer = server;
        this.connection = new TaskRepository.CancellableConnection() {
            protected void doTest() throws Exception {
                TestGitLabServerConnectionTask.this.project
                        .getComponent(GitLabServerManager.class)
                        .getGitLabRestApiFrom(gitLabServer)
                        .getMarkdownApi()
                        .getMarkdown("test");
            }

            public void cancel() {
            }
        };
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setText("Connecting to " + gitLabServer.getUrl() + "...");
        indicator.setFraction(0);
        indicator.setIndeterminate(true);
        try {

            Future<Exception> future = ApplicationManager.getApplication().executeOnPooledThread(connection);
            while (true) {
                try {
                    exception = future.get(100, TimeUnit.MILLISECONDS);
                    return;
                } catch (TimeoutException ignore) {
                    try {
                        indicator.checkCanceled();
                    } catch (ProcessCanceledException e) {
                        exception = e;
                        connection.cancel();
                        return;
                    }
                } catch (Exception e) {
                    exception = e;
                    return;
                }
            }
        } catch (Exception e) {
            exception = e;
        }
    }

    @Override
    public void onCancel() {
        if (connection != null) {
            connection.cancel();
        }
    }

    public Exception getException() {
        return exception;
    }
}

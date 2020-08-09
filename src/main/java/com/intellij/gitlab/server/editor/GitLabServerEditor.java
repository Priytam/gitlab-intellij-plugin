package com.intellij.gitlab.server.editor;

import com.intellij.gitlab.server.GitLabServer;
import com.intellij.gitlab.server.auth.AuthType;
import com.intellij.gitlab.ui.GitLabTabbedPane;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GitLabServerEditor {

    private final Project project;
    private final GitLabServer server;
    private final boolean selectedServer;

    private BiConsumer<GitLabServer, Boolean> changeListener;
    private Consumer<GitLabServer> changeUrlListener;

    private GitLabTabbedPane gitLabTabbedPane;

    public GitLabServerEditor(Project project, GitLabServer server, boolean selected, BiConsumer<GitLabServer, Boolean> changeListener, Consumer<GitLabServer> changeUrlListener) {
        this.project = project;
        this.server = server;
        this.selectedServer = selected;
        this.changeListener = changeListener;
        this.changeUrlListener = changeUrlListener;
    }

    public JPanel getPanel(){
        GitLabServerAuthEditor userAndPassAuthEditor = new GitLabServerUserAndPassAuthEditor(project, server, selectedServer, changeListener, changeUrlListener);
        GitLabServerAuthEditor apiTokenAuthEditor = new GitLabServerAPITokenAuthEditor(project, server, selectedServer, changeListener, changeUrlListener);

        this.gitLabTabbedPane = new GitLabTabbedPane(JTabbedPane.NORTH);
        this.gitLabTabbedPane.addTab("User And Pass", userAndPassAuthEditor.getPanel());
        this.gitLabTabbedPane.addTab("API Token", apiTokenAuthEditor.getPanel());

        if (AuthType.API_TOKEN == server.getType()) {
            this.gitLabTabbedPane.setSelectedIndex(1);
        }

        return FormBuilder.createFormBuilder()
                .addComponent(this.gitLabTabbedPane)
                .getPanel();
    }

}

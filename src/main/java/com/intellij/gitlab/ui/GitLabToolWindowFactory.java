package com.intellij.gitlab.ui;

import com.intellij.gitlab.server.GitLabServerManager;
import com.intellij.gitlab.ui.panels.mergerequest.GitLabMergeRequestPanel;
import com.intellij.gitlab.ui.panels.pipeline.GitLabPipelinesPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.gitlab4j.api.GitLabApi;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GitLabToolWindowFactory implements ToolWindowFactory {

    public static final String TOOL_WINDOW_ID = "GITLAB";
    public static final String TAB_PIPELINE = "Pipelines";
    public static final String TAB_MERGE_REQUESTS = "Merge Requests";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        createContent(project, toolWindow);

        project.getComponent(GitLabServerManager.class).addConfigurationServerChangedListener(() -> {
            SwingUtilities.invokeLater(() -> createContent(project, toolWindow));
        });

        toolWindow.setType(ToolWindowType.DOCKED, null);
    }

    private void createContent(Project project, ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);

        GitLabApi gitLabApi = project.getComponent(GitLabServerManager.class).getGitLabApi();
        ContentFactory factory = contentManager.getFactory();

        GitLabPipelinesPanel gitLabPipelinesPanel = new GitLabPipelinesPanel(gitLabApi, project);
        Content pipelineContent = factory.createContent(gitLabPipelinesPanel, TAB_PIPELINE, false);
        contentManager.addDataProvider(gitLabPipelinesPanel);
        contentManager.addContent(pipelineContent);


        GitLabMergeRequestPanel gitLabMergeRequestPanel = new GitLabMergeRequestPanel(gitLabApi, project);
        Content mrContent = factory.createContent(gitLabMergeRequestPanel, TAB_MERGE_REQUESTS, false);
        contentManager.addDataProvider(gitLabMergeRequestPanel);
        contentManager.addContent(mrContent);
    }
}

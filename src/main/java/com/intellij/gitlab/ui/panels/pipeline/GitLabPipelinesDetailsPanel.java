package com.intellij.gitlab.ui.panels.pipeline;

import com.intellij.gitlab.ui.GitLabTabbedPane;
import com.intellij.gitlab.util.GitLabPanelUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import org.gitlab4j.api.models.Pipeline;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

public class GitLabPipelinesDetailsPanel extends SimpleToolWindowPanel {

    private static final String TAB_KEY = "selectedTab";

    private final Project project;
    private final Map<String, Integer> data = new HashMap<>();

    public GitLabPipelinesDetailsPanel(Project project){
        super(true);
        this.project = project;
        setEmptyContent();
    }

    public void showPipeLines(@Nullable Pipeline pipeline, org.gitlab4j.api.models.Project selectedProject) {
        if(isNull(pipeline)){
            setEmptyContent();
        }else{
            GitLabTabbedPane tabbedPane = new GitLabTabbedPane(JTabbedPane.BOTTOM);
            tabbedPane.addTab("Preview", new GitLabPipelinePreviewPanel(project, selectedProject, pipeline));
            tabbedPane.addTab("Jobs", new GitLabPipeLineJobsPanel(project, selectedProject, pipeline));
            tabbedPane.addChangeListener(e -> data.put(TAB_KEY, tabbedPane.getSelectedIndex()));
            tabbedPane.setSelectedIndex(data.getOrDefault(TAB_KEY, 0));
            setContent(tabbedPane);
        }

    }

    public void setEmptyContent(){
        setContent(GitLabPanelUtil.createPlaceHolderPanel("Select issue to view details"));
    }

}

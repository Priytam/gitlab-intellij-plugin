package com.intellij.gitlab.actions;

import com.intellij.icons.AllIcons;
import com.intellij.gitlab.server.GitLabServerManager;
import com.intellij.gitlab.ui.panels.pipeline.GitLabPipelinesPanel;
import com.intellij.gitlab.ui.popup.GoToPipelinePopup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class GoToPipelinePopupAction extends GitLabPipelineAction {
    private static final ActionProperties properties = ActionProperties.of("Go to",  AllIcons.Actions.Find, "control shift G");

    public GoToPipelinePopupAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(isNull(project)){
            return;
        }

        GitLabPipelinesPanel pipelinePanel = (GitLabPipelinesPanel) getComponent();
        if(isNull(pipelinePanel)){
            return;
        }

        List<String> ids = pipelinePanel.getTableListModel().getItems().stream().map(pipeline -> String.valueOf(pipeline.getId())).collect(toList());
        GoToPipelinePopup popup = new GoToPipelinePopup(project, ids, pipelinePanel::goToPipeline);
        popup.show(pipelinePanel.getPipelineTable());

    }

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            event.getPresentation().setEnabled(false);
        } else {
            GitLabServerManager manager = project.getComponent(GitLabServerManager.class);
            if(manager.hasGitLabServerConfigured()){
                event.getPresentation().setEnabled(true);
            }
            else{
                event.getPresentation().setEnabled(false);
            }
        }
    }



}

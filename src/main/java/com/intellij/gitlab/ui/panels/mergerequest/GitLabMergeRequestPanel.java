package com.intellij.gitlab.ui.panels.mergerequest;

import com.intellij.gitlab.actions.ConfigureGitLabServersAction;
import com.intellij.gitlab.actions.GitLabPipelineActionGroup;
import com.intellij.gitlab.actions.GoToPipelinePopupAction;
import com.intellij.gitlab.components.GitLabActionManager;
import com.intellij.gitlab.components.GitLabMergeRequestUpdater;
import com.intellij.gitlab.events.MergeRequestEventListener;
import com.intellij.gitlab.util.GitLabPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.MergeRequest;

import javax.swing.*;
import java.util.List;

import static com.intellij.gitlab.ui.GitLabToolWindowFactory.TOOL_WINDOW_ID;

public class GitLabMergeRequestPanel extends SimpleToolWindowPanel implements MergeRequestEventListener {

    private final GitLabApi gitLabRestApi;
    private final Project project;

    public GitLabMergeRequestPanel(GitLabApi api, Project project) {
        super(false, true);
        this.gitLabRestApi = api;
        this.project = project;
        init();
    }

    private void init() {
        setToolbar();
        setContent();
        addListeners();
    }

    private void addListeners() {
        project.getComponent(GitLabMergeRequestUpdater.class).addListener(this);
    }

    private void setContent() {
        JComponent content = GitLabPanelUtil.createPlaceHolderPanel("Under construction");
        super.setContent(content);
    }


    private void setToolbar() {
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(TOOL_WINDOW_ID, createActionGroup(), false);
        actionToolbar.setTargetComponent(this);
        Box toolBarBox = Box.createHorizontalBox();
        toolBarBox.add(actionToolbar.getComponent());
        super.setToolbar(toolBarBox);
    }

    private ActionGroup createActionGroup() {
        GitLabPipelineActionGroup group = new GitLabPipelineActionGroup(this);
        group.add(GitLabActionManager.getInstance().getGitLabPipelineRefreshAction());
        group.add(new GoToPipelinePopupAction());
        group.add(Separator.getInstance());
        group.add(new ConfigureGitLabServersAction());
        return group;
    }


    @Override
    public void update(List<MergeRequest> mergeRequests) {

    }

    @Override
    public void update(MergeRequest pipeline) {

    }

}

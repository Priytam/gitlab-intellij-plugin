package com.intellij.gitlab.ui.panels.pipeline;

import com.google.common.util.concurrent.SettableFuture;
import com.intellij.gitlab.actions.ConfigureGitLabServersAction;
import com.intellij.gitlab.actions.GitLabPipelineActionGroup;
import com.intellij.gitlab.actions.GoToPipelinePopupAction;
import com.intellij.gitlab.components.GitLabActionManager;
import com.intellij.gitlab.components.GitLabPipelineUpdater;
import com.intellij.gitlab.events.PipelineEventListener;
import com.intellij.gitlab.ui.table.GitLabPipelineListTableModel;
import com.intellij.gitlab.ui.table.GitLabPipelineTableView;
import com.intellij.gitlab.util.GitLabPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.openapi.vcs.impl.VcsInitObject;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.JBUI;
import git4idea.GitVcs;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.commons.compress.utils.Lists;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Pipeline;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

import static com.intellij.gitlab.ui.GitLabToolWindowFactory.TOOL_WINDOW_ID;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class GitLabPipelinesPanel extends SimpleToolWindowPanel implements PipelineEventListener {

    private final GitLabApi gitLabRestApi;
    private final Project project;

    private GitLabPipelineTableView pipelineTable;
    private GitLabPipelinesDetailsPanel pipelinesDetailsPanel;
    private org.gitlab4j.api.models.Project selectedProject;
    private String gitProjectName;

    public GitLabPipelinesPanel(GitLabApi api, Project project) {
        super(false, true);
        this.gitLabRestApi = api;
        this.project = project;
        getVcsProjectAndSetContent();
        init();
    }

    private void init() {
        setToolbar();
        addListeners();
    }

    private void addListeners() {
        project.getComponent(GitLabPipelineUpdater.class).addListener(this);
    }

    private void setContent() {
        JComponent content;
        if(isNull(gitProjectName)) {
            content = GitLabPanelUtil.createPlaceHolderPanel("This project is not a vcs repository");
        } else if (isNull(gitLabRestApi)) {
            content = GitLabPanelUtil.createPlaceHolderPanel("Please configure gitlab server");
        } else {
            List<Pipeline> pipelines = getPipelines();
            pipelinesDetailsPanel = new GitLabPipelinesDetailsPanel(project);

            pipelineTable = new GitLabPipelineTableView(pipelines);
            pipelineTable
                    .getSelectionModel()
                    .addListSelectionListener(event ->
                            SwingUtilities.invokeLater(() ->
                                    this.pipelinesDetailsPanel
                                            .showPipeLines(pipelineTable.getSelectedObject(), selectedProject)));


            JPanel pipelinePanel = new JPanel(new BorderLayout());
            pipelinePanel.setBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 0, 1));
            pipelinePanel.add(ScrollPaneFactory.createScrollPane(pipelineTable), BorderLayout.CENTER);

            JBSplitter splitter = new JBSplitter();
            splitter.setProportion(0.6f);
            splitter.setFirstComponent(pipelinePanel);
            splitter.setSecondComponent(pipelinesDetailsPanel);
            splitter.setShowDividerIcon(false);
            splitter.setDividerWidth(1);

            content = splitter;
        }

        super.setContent(content);

    }

    private void getVcsProjectAndSetContent() {
        ProjectLevelVcsManagerImpl vcsManager = (ProjectLevelVcsManagerImpl) ServiceManager.getService(project, ProjectLevelVcsManager.class);
        GitRepositoryManager gitRepoManager = ServiceManager.getService(project, GitRepositoryManager.class);;
        vcsManager.addInitializationRequest(VcsInitObject.AFTER_COMMON, () -> {
            VirtualFile[] gitRoots = vcsManager.getRootsUnderVcs(GitVcs.getInstance(project));
            for (VirtualFile root : gitRoots) {
                GitRepository repo = gitRepoManager.getRepositoryForRoot(root);
                if (repo != null) {
                   gitProjectName =  repo.getProject().getName();
                }
            }
            setContent();
        });
    }

    private List<Pipeline> getPipelines() {
        try {
            selectedProject = gitLabRestApi.getProjectApi().getProjects(gitProjectName).get(0);
            return gitLabRestApi.getPipelineApi().getPipelines(selectedProject);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
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
    public void update(List<Pipeline> pipelines) {
        if (nonNull(pipelineTable)) {
            Pipeline lastSelectedPipeline = pipelineTable.getSelectedObject();
            pipelineTable.updateModel(pipelines);
            int currentPosPipeLine = pipelineTable.getModel().indexOf(lastSelectedPipeline);
            // if the last selected issue exist in the new list
            if (currentPosPipeLine >= 0) {
                Pipeline pipeline = pipelineTable.getModel().getItem(currentPosPipeLine);
                pipelineTable.addSelection(pipeline);
                pipelinesDetailsPanel.showPipeLines(pipeline, selectedProject);
            } else {
                pipelinesDetailsPanel.setEmptyContent();
            }
        }
    }

    @Override
    public void update(Pipeline pipeline) {
        if (nonNull(pipelineTable)) {
            int postItem = pipelineTable.getModel().indexOf(pipeline);
            if (postItem < 0) {
                return;
            }

            pipelineTable.getModel().removeRow(postItem);
            pipelineTable.getModel().insertRow(postItem, pipeline);
            pipelineTable.addSelection(pipeline);
            pipelinesDetailsPanel.showPipeLines(pipeline, selectedProject);
        }
    }


    public GitLabPipelineListTableModel getTableListModel() {
        return pipelineTable.getModel();
    }

    public GitLabPipelineTableView getPipelineTable() {
        return pipelineTable;
    }

    public Future<Boolean> goToPipeline(String id) {
        SettableFuture<Boolean> future = SettableFuture.create();
        future.set(false);
        Optional<Pipeline> targetPipeline = pipelineTable.getItems().stream().filter(pipeline -> Objects.equals(pipeline.getId(), id)).findFirst();
        if (targetPipeline.isPresent()) {
            future.set(true);
            pipelineTable.addSelection(targetPipeline.get());
            pipelineTable.scrollRectToVisible(pipelineTable.getCellRect(pipelineTable.getSelectedRow(), pipelineTable.getSelectedColumn(), true));
            pipelinesDetailsPanel.showPipeLines(targetPipeline.get(), selectedProject);
        }

        return future;
    }
}

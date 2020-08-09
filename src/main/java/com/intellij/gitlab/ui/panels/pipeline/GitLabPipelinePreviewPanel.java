package com.intellij.gitlab.ui.panels.pipeline;

import com.intellij.gitlab.actions.GitLabPipelineActionGroup;
import com.intellij.gitlab.actions.GitLabPipelineTaskAction;
import com.intellij.gitlab.server.GitLabServerManager;
import com.intellij.gitlab.ui.GitLabTextPane;
import com.intellij.gitlab.ui.panels.AbstractGitLabPanel;
import com.intellij.gitlab.util.GitLabIconUtil;
import com.intellij.gitlab.util.GitLabLabelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Pipeline;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

import static com.intellij.gitlab.util.GitLabLabelUtil.BOLD;
import static com.intellij.gitlab.util.GitLabLabelUtil.DACULA_DEFAULT_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.WHITE;
import static com.intellij.gitlab.util.GitLabLabelUtil.createEmptyLabel;
import static com.intellij.gitlab.util.GitLabLabelUtil.createIconLabel;
import static com.intellij.gitlab.util.GitLabLabelUtil.createLabel;
import static com.intellij.gitlab.util.GitLabLabelUtil.createLinkLabel;
import static com.intellij.gitlab.util.GitLabPanelUtil.MARGIN_BOTTOM;
import static com.intellij.gitlab.util.GitLabPanelUtil.createWhitePanel;
import static com.intellij.gitlab.util.GitLabUtil.getPrettyDateTime;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.LINE_START;
import static java.awt.BorderLayout.PAGE_START;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class GitLabPipelinePreviewPanel extends AbstractGitLabPanel<Pipeline> {

    private final Project project;
    private final org.gitlab4j.api.models.Project selectedProject;
    private Pipeline pipeline;

    GitLabPipelinePreviewPanel(@NotNull Project project, org.gitlab4j.api.models.Project selectedProject, @NotNull Pipeline pipeline) {
        super(true, pipeline);
        this.project = project;
        this.pipeline = getPipelineDetail(selectedProject, pipeline);
        this.selectedProject = selectedProject;
        setBackground(JBColor.white);
        initContent();
    }

    @Override
    public ActionGroup getActionGroup() {
        GitLabPipelineActionGroup group = new GitLabPipelineActionGroup(this);
        group.add(new GitLabPipelineTaskAction(GitLabPipelineTaskAction.PipelineTask.CANCEL_TASK, () -> selectedProject, () -> pipeline));
        group.add(new GitLabPipelineTaskAction(GitLabPipelineTaskAction.PipelineTask.RETRY_TASK, () -> selectedProject, () -> pipeline));
        group.add(new GitLabPipelineTaskAction(GitLabPipelineTaskAction.PipelineTask.DELETE_PIPELINE, () -> selectedProject, () -> pipeline));
        return group;
    }

    private void initContent() {
        JPanel pipelineDetails = new JBPanel().withBackground(JBColor.WHITE);
        pipelineDetails.setLayout(new BoxLayout(pipelineDetails, Y_AXIS));

        //ID
        JPanel pipelineIdPanel = new JBPanel().withBackground(JBColor.WHITE);
        pipelineIdPanel.setLayout(new BoxLayout(pipelineIdPanel, X_AXIS));
        JBLabel pipelineIdLabel = createLinkLabel(String.valueOf(pipeline.getId()), pipeline.getWebUrl());
        pipelineIdPanel.add(pipelineIdLabel);

        //Duration
        JBLabel durationPanel = createLabel(getDuration()).withFont(BOLD);
        durationPanel.setForeground(JBColor.darkGray);
        durationPanel.setToolTipText("Duration");

        // id and created
        JPanel idAndUpdatedPanel = createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        idAndUpdatedPanel.add(pipelineIdPanel);
        idAndUpdatedPanel.add(durationPanel);
        pipelineDetails.add(idAndUpdatedPanel);

        // Ref
        if (!isNull(pipeline.getRef())) {
            JTextArea refArea = new JTextArea(pipeline.getRef());
            refArea.setLineWrap(true);
            refArea.setWrapStyleWord(true);
            refArea.setEditable(false);
            refArea. setBackground(UIUtil.isUnderDarcula() ? DACULA_DEFAULT_COLOR : WHITE);

            JPanel pipelineRefPanel = createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);
            pipelineRefPanel.add(refArea, CENTER);
            pipelineDetails.add(pipelineRefPanel);
        }

        // Created
        JPanel createdPanel = getPanel("Created: ", pipeline.getCreatedAt());

        // Updated
        JPanel updatedPanel = getPanel("Updated: ", pipeline.getCreatedAt());


        // Created and Updated
        JPanel createdAndUpdated = createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        createdAndUpdated.add(createdPanel);
        createdAndUpdated.add(updatedPanel);
        pipelineDetails.add(createdAndUpdated);


        // Started
        JPanel typePanel = getPanel("Started: ", pipeline.getStartedAt());

        // Finished
        JPanel finishedPanel = getPanel("Started: ", pipeline.getFinishedAt());

        // Started and Finished
        JPanel startedAndFinishedPanel = createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        startedAndFinishedPanel.add(typePanel);
        startedAndFinishedPanel.add(finishedPanel);
        pipelineDetails.add(startedAndFinishedPanel);

        // Status
        JPanel statusPanel = createWhitePanel(new BorderLayout());
        statusPanel.setLayout(new BoxLayout( statusPanel, X_AXIS));
        JBLabel priorityLabel = createLabel("Status: ").withFont(BOLD);
        JLabel statusValueLabel = GitLabLabelUtil.createStatusLabel1(pipeline.getStatus());
        statusPanel.add(priorityLabel, LINE_START);
        statusPanel.add(statusValueLabel, CENTER);

        // User
        JPanel userPanel = createWhitePanel(new BorderLayout());
        JBLabel userLabel = createLabel("User: ").withFont(BOLD);
        JBLabel userValueLabel = nonNull(pipeline.getUser()) ? createIconLabel(GitLabIconUtil.getSmallIcon(pipeline.getUser().getAvatarUrl()), pipeline.getUser().getName()) : createEmptyLabel();
        userPanel.add(userLabel, LINE_START);
        userPanel.add(userValueLabel, CENTER);

        // Status and User
        JPanel statusAndUser = createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        statusAndUser.add(statusPanel);
        statusAndUser.add(userPanel);
        pipelineDetails.add(statusAndUser);

        // Error
        if (StringUtil.isNotEmpty(pipeline.getYamlErrors())) {
            JBLabel descriptionLabel = createLabel("Ymal Error: ").withFont(BOLD);
            GitLabTextPane ymalErrorTextPane = new GitLabTextPane();
            ymalErrorTextPane.setHTMLText(pipeline.getYamlErrors());

            JPanel pipelineYmalError = createWhitePanel(new BorderLayout());
            pipelineYmalError.add(descriptionLabel, PAGE_START);
            pipelineYmalError.add(ymalErrorTextPane, CENTER);
            pipelineDetails.add(pipelineYmalError);
        }

        JPanel previewPanel = new JBPanel(new BorderLayout())
                .withBackground(JBColor.WHITE)
                .withBorder(JBUI.Borders.empty(1, 5, 1, 0));
        previewPanel.add(ScrollPaneFactory.createScrollPane(pipelineDetails, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);
        setContent(previewPanel);
    }

    private String getDuration() {
        Integer secs = pipeline.getDuration();
        return (secs / 3600) + ":" + ((secs % 3600) / 60) +":"+ (secs % 60);
    }

    @NotNull
    private JPanel getPanel(String s, Date startedAt) {
        JPanel typePanel = createWhitePanel(new BorderLayout());
        JBLabel typeLabel = createLabel(s).withFont(BOLD);
        JBLabel typeValueLabel = createLabel(getPrettyDateTime(startedAt));
        typePanel.add(typeLabel, LINE_START);
        typePanel.add(typeValueLabel, CENTER);
        return typePanel;
    }

    private Pipeline getPipelineDetail(org.gitlab4j.api.models.Project selectedProject, Pipeline pipeline) {
        try {
            GitLabApi gitLabApi = project.getComponent(GitLabServerManager.class).getGitLabApi();
            return gitLabApi.getPipelineApi().getPipeline(selectedProject, pipeline.getId());
        } catch (Exception e) {
            return pipeline;
        }
    }
}

package com.intellij.gitlab.ui.panels.pipeline;

import com.google.common.collect.Lists;
import com.intellij.gitlab.actions.GitLabPipelineActionGroup;
import com.intellij.gitlab.server.GitLabServerManager;
import com.intellij.gitlab.tasks.JobRetryTask;
import com.intellij.gitlab.ui.panels.AbstractGitLabPanel;
import com.intellij.gitlab.util.GitLabLabelUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import org.apache.commons.collections.CollectionUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.JobStatus;
import org.gitlab4j.api.models.Pipeline;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intellij.gitlab.util.GitLabLabelUtil.BOLD;
import static com.intellij.gitlab.util.GitLabLabelUtil.CANCELED_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.DONE_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.FAILED_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.HAND_CURSOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.IN_PROGRESS_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.MANUAL_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.PENDING_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.SKIPPED_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.UNDEFINED_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.createEmptyLabel;
import static com.intellij.gitlab.util.GitLabLabelUtil.createEmptyStatusLabel;
import static com.intellij.gitlab.util.GitLabLabelUtil.createLabel;
import static com.intellij.gitlab.util.GitLabLabelUtil.createLinkLabel;
import static com.intellij.gitlab.util.GitLabPanelUtil.MARGIN_BOTTOM;
import static com.intellij.gitlab.util.GitLabPanelUtil.createWhitePanel;
import static com.intellij.openapi.util.text.StringUtil.toUpperCase;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.LINE_START;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class GitLabPipeLineJobsPanel extends AbstractGitLabPanel<Pipeline> {


    private final org.gitlab4j.api.models.Project selectedProject;
    private final Project project;
    private final Pipeline pipeline;

    public GitLabPipeLineJobsPanel(Project project, org.gitlab4j.api.models.Project selectedProject, Pipeline pipeline) {
        super(true, true, pipeline);
        this.project = project;
        this.selectedProject = selectedProject;
        this.pipeline = pipeline;
        setBackground(JBColor.white);
        initContent(getJobs());
    }

    @Override
    public ActionGroup getActionGroup() {
        GitLabPipelineActionGroup group = new GitLabPipelineActionGroup(this);
        //group.add(new TransitIssueDialogAction(() -> null));
        //group.add(new GitLabPipelineAssigneePopupAction(() -> null));
        //group.add(new GitLabPipelinePrioritiesPopupAction(() -> null));
        return group;
    }

    private void initContent(List<Job> jobs) {

        JPanel jobDetails = new JBPanel().withBackground(JBColor.WHITE);
        jobDetails.setLayout(new BoxLayout(jobDetails, Y_AXIS));

        if (CollectionUtils.isNotEmpty(jobs)) {
            Map<String, List<Job>> groupedJobs = getGroupedJobs(jobs);
            groupedJobs.forEach((stage, jobList) -> {
                JPanel stagePanel = getStagePanel(stage);
                jobDetails.add(stagePanel);
                jobList.forEach(job -> {
                    JPanel jobPanel = prepareJobPanel(job);
                    jobDetails.add(jobPanel);
                });
            });
        }


        JBPanel panel = new JBPanel(new BorderLayout())
                .withBackground(JBColor.WHITE)
                .withBorder(JBUI.Borders.empty(1, 0));
        panel.add(ScrollPaneFactory.createScrollPane(jobDetails, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);
        setContent(panel);
    }

    private JPanel prepareJobPanel(Job job) {
        JPanel panel = createWhitePanel(new GridLayout(1, 4)).withBorder(MARGIN_BOTTOM);
        panel.add(createLinkLabel(String.valueOf(job.getId()), job.getWebUrl()));
        panel.add(createLabel(job.getName()));
        panel.add(createLabel(getDuration(job.getDuration())));
        panel.add(createStatusLabel(job));
        panel.add(createActionButton(job));
        return panel;
    }

    private Component createActionButton(Job job) {
        if (job.getStatus().equals(JobStatus.SUCCESS) || job.getStatus().equals(JobStatus.FAILED)) {
            return getButtonLabel("Retry", job, AllIcons.Actions.Restart);
        }

        if (job.getStatus().equals(JobStatus.MANUAL)) {
            return getButtonLabel("Play", job, AllIcons.RunConfigurations.TestState.Run);
        }
        return createEmptyLabel();
    }

    @NotNull
    private JBLabel getButtonLabel(String text, Job job, Icon icon) {
        JBLabel watchLabel = createLabel(text);
        watchLabel.setBackground(JBColor.WHITE);
        watchLabel.setBorder(JBUI.Borders.empty(2, 20));
        watchLabel.setCursor(HAND_CURSOR);
        watchLabel.setIcon(icon);
        watchLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> new JobRetryTask(project, job.getId(), selectedProject).queue());
            }
        });
        return watchLabel;
    }

    private Component createStatusLabel(Job job) {
        JobStatus status = job.getStatus();
        JBLabel statusLabel = createEmptyStatusLabel();
        statusLabel.setText(toUpperCase(status.toValue()));
        statusLabel.setBackground(getBackgroundColor(status));
        statusLabel.setForeground(GitLabLabelUtil.WHITE);
        return statusLabel;
    }

    private Color getBackgroundColor(JobStatus status) {
        switch (status) {
            case FAILED:
                return FAILED_COLOR;
            case MANUAL:
                return MANUAL_COLOR;
            case RUNNING:
                return IN_PROGRESS_COLOR;
            case PENDING:
                return PENDING_COLOR;
            case SUCCESS:
                return DONE_COLOR;
            case CANCELED:
                return CANCELED_COLOR;
            case SKIPPED:
                return SKIPPED_COLOR;
            default:
                return UNDEFINED_COLOR;
        }
    }

    private String getDuration(Float floatValue) {
        int secs = floatValue == null ? 0 : floatValue.intValue();
        return (secs / 3600) + ":" + ((secs % 3600) / 60) + ":" + (secs % 60);
    }

    @NotNull
    private Map<String, List<Job>> getGroupedJobs(List<Job> jobs) {
        return jobs.stream().collect(Collectors.toMap(Job::getStage, Lists::newArrayList, (j1, j2) -> {
            j1.addAll(j2);
            return j1;
        }));
    }

    private JPanel getStagePanel(String name) {
        JPanel typePanel = createWhitePanel(new BorderLayout())
                .withBackground(JBColor.LIGHT_GRAY)
                .withBorder(JBUI.Borders.empty(4, 30));
        JBLabel typeLabel = createLabel(name).withFont(BOLD);
        typePanel.add(typeLabel, LINE_START);
        return typePanel;
    }


    private List<Job> getJobs() {
        try {
            GitLabApi gitLabApi = project.getComponent(GitLabServerManager.class).getGitLabApi();
            return gitLabApi.getJobApi().getJobsForPipeline(selectedProject, pipeline.getId());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

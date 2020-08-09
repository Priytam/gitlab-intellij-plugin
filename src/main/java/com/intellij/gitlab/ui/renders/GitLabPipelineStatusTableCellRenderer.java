package com.intellij.gitlab.ui.renders;

import com.intellij.gitlab.util.GitLabLabelUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import org.gitlab4j.api.models.PipelineStatus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.gitlab.util.GitLabLabelUtil.CANCELED_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.DONE_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.FAILED_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.IN_PROGRESS_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.MANUAL_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.PENDING_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.SKIPPED_COLOR;
import static com.intellij.gitlab.util.GitLabLabelUtil.UNDEFINED_COLOR;
import static java.awt.BorderLayout.LINE_START;

public class GitLabPipelineStatusTableCellRenderer extends GitLabPipelineTableCellRenderer {

    private PipelineStatus pipelineStatus;

    public GitLabPipelineStatusTableCellRenderer(PipelineStatus pipelineStatus) {
        super();
        this.pipelineStatus = pipelineStatus;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component label = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);

        JBPanel panel = new JBPanel(new BorderLayout()).withBackground(label.getBackground());
        if(!isSelected){
            panel.withBackground(GitLabLabelUtil.getBgRowColor());
        }

        setText(StringUtil.toUpperCase(pipelineStatus.toValue()));
        setBackground(getBackGround());
        setForeground(getForeGround());
        setFont(JBFont.create(new Font("SansSerif", Font.BOLD, 8)));
        setBorder(JBUI.Borders.empty(4, 3));

        panel.setBorder(JBUI.Borders.empty(4, 3));
        panel.add(this, LINE_START);

        return panel;
    }

    private Color getForeGround() {
        return JBColor.WHITE;
    }

    @NotNull
    private Color getBackGround() {
        switch (pipelineStatus) {
            case FAILED :
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

}

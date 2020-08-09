package com.intellij.gitlab.ui.renders;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static com.intellij.gitlab.util.GitLabLabelUtil.getBgRowColor;
import static com.intellij.gitlab.util.GitLabLabelUtil.getFgRowColor;

public class GitLabPipelineTableCellRenderer extends DefaultTableCellRenderer {



    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, false, row, column);

        setBackground(getBgRowColor(isSelected));
        setForeground(getFgRowColor(isSelected));

        return this;
    }
}

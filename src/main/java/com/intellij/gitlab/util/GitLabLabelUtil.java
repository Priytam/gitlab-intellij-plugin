package com.intellij.gitlab.util;

import com.intellij.gitlab.ui.labels.GitLabLinkLabel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.gitlab4j.api.models.PipelineStatus;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingConstants.LEFT;

public class GitLabLabelUtil {

    public static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    public static final JBFont BOLD = JBUI.Fonts.label().asBold();
    public static final JBFont ITALIC = JBUI.Fonts.label().asItalic();

    public static final Color BLACK = Color.BLACK;
    public static final Color WHITE = Color.WHITE;

    public static final Color LINK_COLOR = JBColor.BLUE;

    public static final Color DACULA_DEFAULT_COLOR = new Color(60, 63, 65);

    public static final Color DEFAULT_ISSUE_COLOR = new Color(211, 232, 240);
    public static final Color DEFAULT_SELECTED_ISSUE_COLOR = new Color(26, 125, 196);
    public static final Color DARCULA_ISSUE_COLOR = new Color(114, 62, 133);
    public static final Color DARCULA_SELECTED_ISSUE_COLOR = new Color(108, 32, 133);

    public static final Color DARK_DARCULA_ISSUE_LINK_COLOR = new Color(133, 34, 77);

    public static final Color DARCULA_TEXT_COLOR = new Color(200, 200, 200);

    // Status
    public static final Color IN_PROGRESS_TEXT_COLOR = new Color(89, 67, 0);
    public static final Color DONE_COLOR = new Color(20, 137, 44);
    public static final Color IN_PROGRESS_COLOR = new Color(255, 211, 81);
    public static final Color FAILED_COLOR = new Color(234, 11, 37);
    public static final Color MANUAL_COLOR = new Color(132, 129, 130);
    public static final Color SKIPPED_COLOR = new Color(11, 234, 201);
    public static final Color UNDEFINED_COLOR = new Color(192, 192, 192);
    public static final Color PENDING_COLOR = new Color(227, 119, 119);
    public static final Color CANCELED_COLOR = new Color(112, 126, 229);

    public static JBLabel createEmptyLabel() {
        return createLabel("");
    }

    public static JBLabel createLabel(String text) {
        return createLabel(text, LEFT);
    }

    public static JBLabel createLabel(String text, int horizontalAlignment) {
        JBLabel label = new JBLabel(text);
        label.setHorizontalAlignment(horizontalAlignment);
        return label;
    }

    public static JBLabel createIconLabel(Icon icon, String text) {
        return new JBLabel(text, icon, LEFT);
    }

    public static JBLabel createIconLabel(String iconUrl, String text) {
        return new JBLabel(text, GitLabIconUtil.getIcon(iconUrl), LEFT);
    }

    public static JBLabel createBoldLabel(String text) {
        return createLabel(text).withFont(BOLD);
    }

    public static JBLabel createLinkLabel(String text, String url) {
        return new GitLabLinkLabel(text, url);
    }

    public static JBLabel createStatusLabel1(PipelineStatus status) {
        JBLabel label = new JBLabel(StringUtil.toUpperCase(status.toValue()), LEFT);
        label.setFont(JBFont.create(new Font("SansSerif", Font.BOLD, 9)));
        label.setBorder(JBUI.Borders.empty(2, 2, 2, 3));
        label.setBackground(status.equals(PipelineStatus.FAILED) ? DARK_DARCULA_ISSUE_LINK_COLOR : DONE_COLOR);
        label.setForeground(status.equals(PipelineStatus.RUNNING) ? IN_PROGRESS_TEXT_COLOR : Color.white);
        label.setOpaque(true);
        return label;
    }

    public static JBLabel createEmptyStatusLabel() {
        JBLabel label = new JBLabel("", LEFT);
        label.setFont(JBFont.create(new Font("SansSerif", Font.BOLD, 9)));
        label.setBorder(JBUI.Borders.empty(2, 10));
        label.setOpaque(true);
        return label;
    }

    public static Color getBgRowColor() {
        return UIUtil.isUnderDarcula() ? DARCULA_ISSUE_COLOR : DEFAULT_ISSUE_COLOR;
    }

    public static Color getFgRowColor() {
        return UIUtil.isUnderDarcula() ? DARCULA_TEXT_COLOR : BLACK;
    }

    public static Color getBgSelectedRowColor() {
        return UIUtil.isUnderDarcula() ? DARCULA_SELECTED_ISSUE_COLOR : DEFAULT_SELECTED_ISSUE_COLOR;
    }

    public static Color getFgSelectedRowColor() {
        return UIUtil.isUnderDarcula() ? DARCULA_TEXT_COLOR : WHITE;
    }

    public static Color getBgRowColor(boolean isSelected) {
        return isSelected ? getBgSelectedRowColor() : getBgRowColor();
    }

    public static Color getFgRowColor(boolean isSelected) {
        return isSelected ? getFgSelectedRowColor() : getFgRowColor();
    }


}

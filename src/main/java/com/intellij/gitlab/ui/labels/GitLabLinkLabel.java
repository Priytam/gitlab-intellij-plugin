package com.intellij.gitlab.ui.labels;


import com.intellij.ide.BrowserUtil;
import com.intellij.gitlab.util.GitLabLabelUtil;
import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GitLabLinkLabel extends JBLabel {

    private String url;

    public GitLabLinkLabel(String text, String url) {
        super(text);
        this.url = url;
        init();
    }

    private void init(){
        setHorizontalAlignment(SwingUtilities.LEFT);
        setToolTipText(this.url);
        setCursor(GitLabLabelUtil.HAND_CURSOR);
        setForeground(GitLabLabelUtil.LINK_COLOR);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> BrowserUtil.open(url));
            }
        });
    }

}

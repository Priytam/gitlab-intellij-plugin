package com.intellij.gitlab.util;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static javax.swing.SwingConstants.CENTER;

public class GitLabPanelUtil {

    public static final Border MARGIN_BOTTOM = JBUI.Borders.emptyBottom(10);

    public static JBPanel createWhitePanel(@NotNull LayoutManager layout){
        return new JBPanel(layout)
                .withBackground(JBColor.WHITE);
    }


    public static JBPanel createPlaceHolderPanel(String text) {
        JBPanel panel = new JBPanel(new GridBagLayout());
        JBLabel messageLabel = new JBLabel(text);
        messageLabel.setHorizontalAlignment(CENTER);
        messageLabel.setVerticalAlignment(CENTER);
        panel.add(messageLabel, new GridBagConstraints());
        return panel;
    }


    public static JPanel createPanelWithVerticalLine(){
        return createPanelWithLine(false);
    }

    public static JPanel createPanelWithHorizontalLine(){
        return createPanelWithLine(true);
    }

    private static JPanel createPanelWithLine(boolean horizontal){
        return new JPanel(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(JBColor.border());
                if(horizontal){
                    g.drawLine(0,getHeight()/2, getWidth(), getHeight()/2);
                }else{
                    g.drawLine(getWidth()/2,0, getWidth()/2, getHeight());
                }
            }
        };
    }





}

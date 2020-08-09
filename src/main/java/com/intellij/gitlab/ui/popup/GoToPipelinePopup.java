package com.intellij.gitlab.ui.popup;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Function;
import com.intellij.util.textCompletion.TextCompletionProvider;
import com.intellij.util.textCompletion.TextFieldWithCompletion;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.concurrent.Future;

public class GoToPipelinePopup {

    private TextFieldWithCompletion textField;
    private JBPopup popup;
    private Function<String, Future> onSelectedIssueKey;


    public GoToPipelinePopup(@NotNull Project project, Collection<String> values, Function<String, Future> onSelectedIssueKey) {
        this.onSelectedIssueKey = onSelectedIssueKey;
        TextCompletionProvider provider = new TextFieldWithAutoCompletion.StringsCompletionProvider(values, null);

        textField = new TextFieldWithCompletion(project, provider, "", true, true, false){

            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onSelectedIssueKey.fun(textField.getText());
                    closePopup();
                    return true;
                }
                return false;
            }
        };


        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setBorder(JBUI.Borders.empty(3));

        JBLabel label = new JBLabel("Enter pipeline id");
        label.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(layout);
        panel.add(label);
        panel.add(textField);
        panel.setBorder(JBUI.Borders.empty(2));

        popup = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, textField)
                .setCancelOnClickOutside(true).setCancelOnWindowDeactivation(true).setCancelKeyEnabled(true)
                .setRequestFocus(true).createPopup();

    }


    public void show(Component component){
        popup.showInCenterOf(component);
    }

    public void closePopup(){
        ApplicationManager.getApplication().invokeLater(() -> popup.closeOk(null));
    }


}

package com.intellij.gitlab.server.editor;

import com.intellij.gitlab.server.GitLabServer;
import com.intellij.gitlab.tasks.TestGitLabServerConnectionTask;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.intellij.gitlab.util.GitLabPanelUtil.MARGIN_BOTTOM;
import static com.intellij.openapi.util.text.StringUtil.trim;

public abstract class GitLabServerAuthEditor {

    protected final static int DEFAULT_WIDTH = 450;
    protected final static int DEFAULT_HEIGHT = 24;

    protected final Project project;
    protected GitLabServer server;
    protected final boolean selectedServer;

    protected BiConsumer<GitLabServer, Boolean> changeListener;
    protected Consumer<GitLabServer> changeUrlListener;

    protected JLabel urlLabel;
    protected JTextField urlField;

    protected JCheckBox defaultServerCheckbox;

    protected JPanel testPanel;
    protected JButton testButton;

    public GitLabServerAuthEditor(Project project, GitLabServer server, boolean selected, BiConsumer<GitLabServer, Boolean> changeListener, Consumer<GitLabServer> changeUrlListener) {
        this.project = project;
        this.server = server;
        this.selectedServer = selected;
        this.changeListener = changeListener;
        this.changeUrlListener = changeUrlListener;
        init();
    }

    public abstract JPanel getPanel();

    public void installListeners() {
        installListener(urlField);
        installListener(defaultServerCheckbox);
        installListener(testButton);
    }

    private void init() {
        this.urlLabel = new JBLabel("Server URL:", 4);
        this.urlField = new JBTextField();
        this.urlField.setText(server.getUrl());
        this.urlField.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        this.defaultServerCheckbox = new JCheckBox("Set Default");
        this.defaultServerCheckbox.setBorder(JBUI.Borders.emptyRight(4));
        this.defaultServerCheckbox.setSelected(selectedServer);

        this.testPanel = new JPanel(new BorderLayout());
        testPanel.setBorder(MARGIN_BOTTOM);
        this.testButton = new JButton("Test");
        this.testPanel.add(testButton, BorderLayout.EAST);
    }


    protected void installListener(JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> apply());
            }
        });
    }

    private void installListener(JCheckBox checkBox) {
        checkBox.addActionListener(e -> defaultServerChanged());
    }

    private void installListener(JButton button){
        button.addActionListener((event) -> SwingUtilities.invokeLater(() -> {
            TestGitLabServerConnectionTask task = new TestGitLabServerConnectionTask(project, server);
            ProgressManager.getInstance().run(task);
            Exception e = task.getException();
            if (e == null) {
                Messages.showMessageDialog(project, "Connection is successful", "Connection", Messages.getInformationIcon());
            }
            else if (!(e instanceof ProcessCanceledException)) {
                String message = e.getMessage();
                if (e instanceof UnknownHostException) {
                    message = "Unknown host: " + message;
                }
                if (message == null) {
                    message = "Unknown error";
                }
                Messages.showErrorDialog(project, StringUtil.capitalize(message), "Error");
            }
        }));
    }

    protected void apply(){
        this.changeUrlListener.accept(server);
    }

    private void defaultServerChanged(){
        this.changeListener.accept(server, defaultServerCheckbox.isSelected());
    }

}

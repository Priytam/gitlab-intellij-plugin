package com.intellij.gitlab.server.editor;

import com.intellij.gitlab.server.GitLabServer;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.intellij.openapi.util.text.StringUtil.trim;
import static java.lang.String.valueOf;

public class GitLabServerAPITokenAuthEditor extends GitLabServerAuthEditor {

    private JLabel emailLabel;
    private JTextField emailField;

    private JLabel apiTokenLabel;
    private JPasswordField apiTokenField;

    public GitLabServerAPITokenAuthEditor(Project project, GitLabServer server, boolean selected, BiConsumer<GitLabServer, Boolean> changeListener, Consumer<GitLabServer> changeUrlListener) {
        super(project, server, selected, changeListener, changeUrlListener);
    }

    @Override
    public JPanel getPanel() {
        this.emailLabel = new JBLabel("Email:", 4);
        this.emailField = new JBTextField();
        this.emailField.setText(server.getUseremail());
        this.emailField.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        this.apiTokenLabel = new JBLabel("API Token:", 4);
        this.apiTokenField = new JPasswordField();
        this.apiTokenField.setText(server.getApiToken());
        this.apiTokenField.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        installListeners();

        return FormBuilder.createFormBuilder()
                .addVerticalGap(10)
                .addLabeledComponent(this.urlLabel, this.urlField)
                .addLabeledComponent(this.emailLabel, this.emailField)
                .addLabeledComponent(this.apiTokenLabel, this.apiTokenField)
                .addComponent(this.defaultServerCheckbox)
                .addComponentToRightColumn(this.testPanel)
                .getPanel();
    }

    @Override
    public void installListeners() {
        super.installListeners();
        installListener(emailField);
        installListener(apiTokenField);
    }

    @Override
    protected void apply() {
        String url = trim(urlField.getText());
        String userEmail = trim(emailField.getText());
        String apiToken = trim(valueOf(apiTokenField.getPassword()));
        this.server.withApiToken(url, userEmail, apiToken);
        super.apply();
    }

}

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

public class GitLabServerUserAndPassAuthEditor extends GitLabServerAuthEditor {

    private JLabel usernameLabel;
    private JTextField usernameField;

    private JLabel passwordLabel;
    private JPasswordField passwordField;

    public GitLabServerUserAndPassAuthEditor(Project project, GitLabServer server, boolean selected, BiConsumer<GitLabServer, Boolean> changeListener, Consumer<GitLabServer> changeUrlListener) {
        super(project, server, selected, changeListener, changeUrlListener);
    }

    @Override
    public JPanel getPanel() {

        this.usernameLabel = new JBLabel("Username:", 4);
        this.usernameField = new JBTextField();
        this.usernameField.setText(server.getUsername());
        this.usernameField.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        this.passwordLabel = new JBLabel("Password:", 4);
        this.passwordField = new JPasswordField();
        this.passwordField.setText(server.getPassword());
        this.passwordField.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        installListeners();

        return FormBuilder.createFormBuilder()
                .addVerticalGap(10)
                .addLabeledComponent(this.urlLabel, this.urlField)
                .addLabeledComponent(this.usernameLabel, this.usernameField)
                .addLabeledComponent(this.passwordLabel, this.passwordField)
                .addComponent(this.defaultServerCheckbox)
                .addComponentToRightColumn(this.testPanel)
                .getPanel();
    }

    @Override
    public void installListeners() {
        super.installListeners();
        installListener(usernameField);
        installListener(passwordField);
    }

    @Override
    protected void apply() {
        String url = trim(urlField.getText());
        String username = trim(usernameField.getText());
        String password = trim(valueOf(passwordField.getPassword()));

        this.server.withUserAndPass(url, username, password);

        super.apply();
    }


}

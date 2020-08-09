package com.intellij.gitlab.actions;

import com.intellij.icons.AllIcons;
import com.intellij.gitlab.ui.dialog.ConfigureGitLabServersDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.isNull;

public class ConfigureGitLabServersAction extends GitLabPipelineAction {

    private static final ActionProperties properties = ActionProperties.of("Configure Servers...",  AllIcons.General.Settings);

    public ConfigureGitLabServersAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(isNull(project)){
            return;
        }

        ConfigureGitLabServersDialog dlg = new ConfigureGitLabServersDialog(project);
        dlg.show();
    }




}

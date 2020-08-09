package com.intellij.gitlab.server;

import com.intellij.gitlab.server.auth.AuthType;
import com.intellij.gitlab.util.SimpleSelectableList;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import org.gitlab4j.api.GitLabApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(name = "GitLabServerManager", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class GitLabServerManager implements ProjectComponent, PersistentStateComponent<GitLabServerManager.Config> {

    private List<Runnable> listeners = new ArrayList<>();
    private SimpleSelectableList<GitLabServer> gitLabServers = new SimpleSelectableList<>();
    private Config config = new Config();

    @Nullable
    @Override
    public Config getState() {
        config.selected = gitLabServers.getSelectedItemIndex();
        config.servers = gitLabServers.getItems();
        return config;
    }

    @Override
    public void loadState(@NotNull Config config) {
        XmlSerializerUtil.copyBean(config, this.config);

        gitLabServers.clear();
        List<GitLabServer> servers = config.servers;
        if (servers != null) {
            gitLabServers.addAll(servers);
        }
        gitLabServers.selectItem(config.selected);
    }

    public void addConfigurationServerChangedListener(Runnable runnable) {
        listeners.add(runnable);
    }

    public List<GitLabServer> getGitLabServers() {
        return gitLabServers.getItems();
    }

    public int getSelectedGitLabServerIndex() {
        return gitLabServers.getSelectedItemIndex();
    }

    public boolean hasGitLabServerConfigured() {
        return gitLabServers.hasSelectedItem();
    }

    public GitLabServer getCurrentGitLabServer() {
        return gitLabServers.hasSelectedItem() ? gitLabServers.getItems().get(getSelectedGitLabServerIndex()) : null;
    }

    public void setGitLabServers(SimpleSelectableList<GitLabServer> servers) {
        this.gitLabServers = servers;
        onServersChanged();
    }

    @Nullable
    public GitLabApi getGitLabApi() {
        GitLabServer currentGitLabServer = getCurrentGitLabServer();
        return null == currentGitLabServer ? null : prepare(currentGitLabServer);
    }

    @NotNull
    public GitLabApi getGitLabRestApiFrom(@NotNull GitLabServer gitLabServer) {
        return prepare(gitLabServer);
    }

    @Nullable
    private GitLabApi prepare(GitLabServer gitLabServer) {
        return gitLabServer.getType().equals(AuthType.API_TOKEN) ?
                new GitLabApi(gitLabServer.getUrl(), gitLabServer.getApiToken()) :
                new GitLabApi(gitLabServer.getUrl(), gitLabServer.getUsername(), gitLabServer.getPassword());
    }


    private void onServersChanged() {
        listeners.forEach(Runnable::run);
    }


    public static class Config {
        @Tag("selected")
        public int selected;
        @XCollection(propertyElementName = "servers")
        public List<GitLabServer> servers;
    }

}

package com.intellij.gitlab.ui.dialog;

import com.intellij.gitlab.server.GitLabServer;
import com.intellij.gitlab.server.editor.GitLabServerEditor;
import com.intellij.gitlab.server.GitLabServerManager;
import com.intellij.gitlab.tasks.RefreshPipelineTask;
import com.intellij.gitlab.util.GitLabPanelUtil;
import com.intellij.gitlab.util.SimpleSelectableList;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.containers.ConcurrentFactoryMap;
import com.intellij.util.ui.UI;
import icons.TasksCoreIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.intellij.openapi.ui.Messages.CANCEL_BUTTON;
import static com.intellij.openapi.ui.Messages.OK_BUTTON;
import static java.util.Objects.nonNull;

public class ConfigureGitLabServersDialog extends DialogWrapper {

    private final static JPanel EMPTY_PANEL = GitLabPanelUtil.createPlaceHolderPanel("No server selected.").withMinimumWidth(450).withMinimumHeight(100);
    private final static String EMPTY_PANEL_NAME = "empty.panel";

    private final Project project;
    private final GitLabServerManager gitLabServerManager;

    private SimpleSelectableList<GitLabServer> servers;
    private JBList<GitLabServer> serversList;
    private final List<GitLabServerEditor> editors = new ArrayList<>();

    private JPanel gitLabServerEditor;
    private Splitter splitter;

    private int count;
    private final Map<GitLabServer, String> serverNamesMap = ConcurrentFactoryMap.createMap(server -> Integer.toString(count++));

    private BiConsumer<GitLabServer, Boolean> changeListener;
    private Consumer<GitLabServer> changeUrlListener;


    public ConfigureGitLabServersDialog(@NotNull Project project) {
        super(project, false);
        this.project = project;
        this.gitLabServerManager = project.getComponent(GitLabServerManager.class);
        init();
    }


    @Override
    protected void init() {
        //editor
        gitLabServerEditor = new JPanel(new CardLayout());
        gitLabServerEditor.add(EMPTY_PANEL, EMPTY_PANEL_NAME);

        // servers
        servers = new SimpleSelectableList<>();
        CollectionListModel listModel = new CollectionListModel(new ArrayList());
        for(GitLabServer server : gitLabServerManager.getGitLabServers()){
            GitLabServer clone = server.clone();
            listModel.add(clone);
            servers.add(clone);
        }

        servers.selectItem(gitLabServerManager.getSelectedGitLabServerIndex());

        //listeners
        this.changeListener = (server, selected) -> servers.updateSelectedItem(server, selected);
        this.changeUrlListener = (server) -> ((CollectionListModel) serversList.getModel()).contentsChanged(server);


        IntStream.range(0, servers.getItems().size())
                .forEach(i -> addGitLabServerEditor(servers.getItems().get(i), i == gitLabServerManager.getSelectedGitLabServerIndex()));


        serversList = new JBList();
        serversList.setEmptyText("No servers");
        serversList.setModel(listModel);
        serversList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        serversList.addListSelectionListener(e -> {
            GitLabServer selectedServer = getSelectedServer();
            if(nonNull(selectedServer)) {
                String name = serverNamesMap.get(selectedServer);
                updateEditorPanel(name);
            }
        });

        serversList.setCellRenderer(new ColoredListCellRenderer() {
            @Override
            protected void customizeCellRenderer(@NotNull JList list, Object value, int index, boolean selected, boolean hasFocus) {
                GitLabServer server = (GitLabServer)value;
                setIcon(TasksCoreIcons.Gitlab);
                append(server.getPresentableName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            }
        });

        setTitle("Configure Servers");
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        splitter = new JBSplitter(true, 0.6f);
        splitter.setFirstComponent(createServersPanel());
        splitter.setSecondComponent(createDetailsServerPanel());

        return splitter;
    }


    @Override
    protected void doOKAction() {
        gitLabServerManager.setGitLabServers(servers);
        updateIssues();

        super.doOKAction();
    }

    private JComponent createServersPanel() {

        if(servers.hasSelectedItem()){
            serversList.setSelectedValue(servers.getSelectedItem(), true);
        }

        JBPanel myPanel = new JBPanel(new BorderLayout());
        myPanel.setMinimumSize(UI.size(-1, 200));
        myPanel.add(ToolbarDecorator.createDecorator(serversList)
                        .setAddAction(button -> {
                            addGitLabServer();
                        })
                        .setRemoveAction(button -> {
                            if (Messages.showOkCancelDialog(project, "You are going to delete this server, are you sure?","Delete Server", OK_BUTTON, CANCEL_BUTTON, Messages.getQuestionIcon()) == Messages.OK) {
                                removeGitLabServer();
                            }
                        })
                        .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        return myPanel;
    }


    private void addGitLabServer(){
        GitLabServer server = new GitLabServer();
        servers.add(server);
        ((CollectionListModel) serversList.getModel()).add(server);
        addGitLabServerEditor(server, false);
        serversList.setSelectedIndex(serversList.getModel().getSize() - 1);
    }

    private void addGitLabServerEditor(GitLabServer server, boolean selected){
        GitLabServerEditor editor = new GitLabServerEditor(project, server, selected, changeListener, changeUrlListener);
        editors.add(editor);
        String name = serverNamesMap.get(server);
        gitLabServerEditor.add(editor.getPanel(), name);
        gitLabServerEditor.doLayout();
    }


    private void removeGitLabServer(){
        int selectedServer = serversList.getSelectedIndex();
        if(selectedServer > -1){
            ((CollectionListModel) serversList.getModel()).remove(selectedServer);
            servers.remove(selectedServer);
            serversList.setSelectedIndex(servers.getSelectedItemIndex());
        }

        if(servers.isEmpty()){
            updateEditorPanel(EMPTY_PANEL_NAME);
        }


    }

    private void updateEditorPanel(String name){
        ((CardLayout) gitLabServerEditor.getLayout()).show(gitLabServerEditor, name);
        splitter.doLayout();
        splitter.repaint();
    }

    private void updateIssues(){
        new RefreshPipelineTask(project).queue();
    }


    private JComponent createDetailsServerPanel() {
        return gitLabServerEditor;
    }

    @Nullable
    private GitLabServer getSelectedServer(){
        return serversList.getSelectedValue();
    }
}

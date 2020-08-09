package com.intellij.gitlab.components;

import com.intellij.gitlab.events.MergeRequestEventListener;
import com.intellij.openapi.components.ProjectComponent;
import org.gitlab4j.api.models.MergeRequest;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GitLabMergeRequestUpdater implements ProjectComponent, Updater<MergeRequest>  {

    private List<MergeRequestEventListener> listeners;

    @Override
    public void projectOpened() {
        listeners = new ArrayList<>();
    }

    @Override
    public void projectClosed() {
        listeners.clear();
    }



    public void addListener(MergeRequestEventListener listener){
        listeners.add(listener);
    }

    @Override
    public void update(List<MergeRequest> mergeRequests) {
        listeners.forEach(j ->
                SwingUtilities.invokeLater(() -> j.update(mergeRequests)));
    }

    @Override
    public void update(MergeRequest mergeRequest) {
        listeners.forEach(j ->
            SwingUtilities.invokeLater(() -> j.update(mergeRequest)));
    }

}

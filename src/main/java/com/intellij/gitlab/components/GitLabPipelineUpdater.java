package com.intellij.gitlab.components;

import com.intellij.gitlab.events.PipelineEventListener;
import com.intellij.openapi.components.ProjectComponent;
import org.gitlab4j.api.models.Pipeline;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GitLabPipelineUpdater implements ProjectComponent, Updater<Pipeline>  {

    private List<PipelineEventListener> listeners;

    @Override
    public void projectOpened() {
        listeners = new ArrayList<>();
    }

    @Override
    public void projectClosed() {
        listeners.clear();
    }



    public void addListener(PipelineEventListener listener){
        listeners.add(listener);
    }

    @Override
    public void update(List<Pipeline> pipelines) {
        listeners.forEach(j ->
                SwingUtilities.invokeLater(() -> j.update(pipelines)));
    }

    @Override
    public void update(Pipeline pipeline) {
        listeners.forEach(j ->
            SwingUtilities.invokeLater(() -> j.update(pipeline)));
    }

}

package com.intellij.gitlab.events;

import com.intellij.gitlab.components.Updater;
import org.gitlab4j.api.models.Pipeline;

public interface PipelineEventListener extends Updater<Pipeline> {

}

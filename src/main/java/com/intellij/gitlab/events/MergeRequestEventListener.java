package com.intellij.gitlab.events;

import com.intellij.gitlab.components.Updater;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Pipeline;

public interface MergeRequestEventListener extends Updater<MergeRequest> {

}

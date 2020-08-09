package com.intellij.gitlab.util.factory;

import com.intellij.openapi.util.Factory;
import org.gitlab4j.api.models.Pipeline;

@FunctionalInterface
public interface GitLabPipelineFactory extends Factory<Pipeline> {

}

package com.intellij.gitlab.util.factory;

import com.intellij.openapi.util.Factory;
import org.gitlab4j.api.models.Project;

@FunctionalInterface
public interface GitLabSelectedProjectFactory extends Factory<Project> {

}

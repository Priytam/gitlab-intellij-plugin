package com.intellij.gitlab.exceptions;

public class GitLabServerConfigurationNotFoundException extends RuntimeException {

    private static final String NO_GITLAB_SERVER_FOUND = "No GitLab server found";

    public GitLabServerConfigurationNotFoundException() {
        super(NO_GITLAB_SERVER_FOUND, new Throwable());
    }

    public GitLabServerConfigurationNotFoundException(String detailMessage) {
        super(NO_GITLAB_SERVER_FOUND, new Throwable(detailMessage));
    }
}

package com.intellij.gitlab.server;

import com.intellij.gitlab.server.auth.AuthType;
import com.intellij.openapi.util.PasswordUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.intellij.openapi.util.text.StringUtil.trim;

@Tag("GitLabServer")
public class GitLabServer {

    private static final AuthType DEFAULT_AUTH_TYPE = AuthType.USER_PASS;

    private String url;

    private String username;
    private String password;

    private String useremail;
    private String apiToken;

    private AuthType type;

    public GitLabServer() { }

    private GitLabServer(String url, String username, String password, String useremail, String apiToken, AuthType type) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.useremail = useremail;
        this.apiToken = apiToken;
        this.type = type;
    }

    private GitLabServer(GitLabServer other){
        this(other.getUrl(), other.getUsername(), other.getPassword(), other.getUseremail(), other.getApiToken(), other.getType());
    }

    public void withUserAndPass(String url, String username, String password) {
        setUrl(url);
        setUsername(username);
        setPassword(password);
        setUseremail(null);
        setApiToken(null);
        setType(AuthType.USER_PASS);
    }

    public void withApiToken(String url, String useremail, String apiToken) {
        setUrl(url);
        setUsername(null);
        setPassword(null);
        setUseremail(useremail);
        setApiToken(apiToken);
        setType(AuthType.API_TOKEN);
    }

    @Attribute("url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Attribute("username")
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Transient
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Attribute("password")
    public String getEncodedPassword() {
        return PasswordUtil.encodePassword(this.getPassword());
    }

    public void setEncodedPassword(String password) {
        try {
            this.setPassword(PasswordUtil.decodePassword(password));
        } catch (NumberFormatException var3) { }
    }

    @Attribute("useremail")
    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    @Transient
    public String getApiToken() {
        return apiToken;
    }

    @Attribute("apiToken")
    public String getEncodedApiToken() {
        return PasswordUtil.encodePassword(this.getApiToken());
    }

    public void setEncodedApiToken(String apiToken) {
        try {
            this.setApiToken(PasswordUtil.decodePassword(apiToken));
        } catch (NumberFormatException var3) { }
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    @Attribute("type")
    public AuthType getType() {
        return type == null ? DEFAULT_AUTH_TYPE : type;
    }

    public void setType(AuthType type) {
        this.type = type == null ? DEFAULT_AUTH_TYPE : type;
    }

    @Transient
    public boolean hasUserAndPassAuth() {
        return AuthType.USER_PASS == getType();
    }

    @Transient
    public String getPresentableName(){
        return StringUtil.isEmpty(trim(getUrl())) ? "<undefined>" : getUrl();
    }

    @NotNull
    @Override
    public GitLabServer clone(){
        return new GitLabServer(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitLabServer that = (GitLabServer) o;
        return url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }


    @Override
    public String toString() {
        return getPresentableName();
    }
}

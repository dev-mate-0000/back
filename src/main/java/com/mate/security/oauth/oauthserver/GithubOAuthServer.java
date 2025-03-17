package com.mate.security.oauth.oauthserver;

import com.mate.security.oauth.OAuthServer;

import java.util.Map;

public class GithubOAuthServer implements OAuthResponse{

    private final Map<String, Object> res;

    public GithubOAuthServer(final Map<String, Object> res) {
        this.res = res;
    }

    @Override
    public OAuthServer getProvider() {
        return OAuthServer.GITHUB;
    }

    @Override
    public String getName() {
        return res.get("name").toString();
    }

    @Override
    public String getGithubUrl() {
        return res.get("html_url").toString();
    }
}

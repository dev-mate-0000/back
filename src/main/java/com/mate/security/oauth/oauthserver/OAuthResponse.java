package com.mate.security.oauth.oauthserver;

import com.mate.security.oauth.OAuthServer;

import java.util.Map;

public interface OAuthResponse {
    Integer getGithubId();
    String getGithubLogin();
    String getGithubUrl();

    String getName();
    String getBio();
    String getEmail();

    OAuthServer getProvider();
    Map<String, Integer>  getLanguages(String githubToken);
}

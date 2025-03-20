package com.mate.security.oauth.oauthserver;

import com.mate.security.oauth.OAuthProvider;

import java.util.Map;

public interface OAuthResponse {
    Integer getGithubId();
    String getGithubLogin();
    String getGithubUrl();

    String getName();
    String getBio();
    String getEmail();

    OAuthProvider getProvider();
    Map<String, Integer>  getLanguages(String githubLogin);
    void setUserInfo(String code);
}

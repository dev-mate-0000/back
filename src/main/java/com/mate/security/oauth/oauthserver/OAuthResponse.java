package com.mate.security.oauth.oauthserver;

import com.mate.security.oauth.OAuthServer;

public interface OAuthResponse {
    OAuthServer getProvider();
    String getName();
    String getGithubUrl();
}

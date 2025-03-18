package com.mate.security;

import com.mate.config.exception.custom.AuthedException;
import com.mate.security.oauth.CustomOAuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private static final String UNAUTHORIZED_USER = "인증되지 않은 사용자입니다";
    private SecurityUtil() {}

    public static CustomOAuthUser getMemberIdByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return ((CustomOAuthUser) authentication.getPrincipal());
        }
        throw new AuthedException(UNAUTHORIZED_USER);
    }
}
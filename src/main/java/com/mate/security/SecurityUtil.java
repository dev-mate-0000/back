package com.mate.security;

import com.mate.security.oauth.CustomOAuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    public static CustomOAuthUser getMemberIdByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return ((CustomOAuthUser) authentication.getPrincipal());
        }
        return null;
    }
}
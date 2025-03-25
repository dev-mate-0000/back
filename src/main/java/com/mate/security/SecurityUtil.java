package com.mate.security;

import com.mate.security.oauth.CustomOAuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtil {

    private SecurityUtil() {}

    public static Optional<CustomOAuthUser> getMemberIdByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuthUser) {
            return Optional.of((CustomOAuthUser) principal);
        }

        return Optional.empty();
    }

}
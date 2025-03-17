package com.mate.security;

import com.mate.security.oauth.CustomOAuthUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${spring.security.redirectURL}")
    private String REDIRECT_URL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuthUser userDetails = (CustomOAuthUser) authentication.getPrincipal();

        String token = jwtUtil.createJwt(userDetails.getId(), userDetails.getName(), jwtUtil.getRefreshExpiredMs());
        Cookie cookie = createCookie(jwtUtil.getRefreshTokenName(), token, jwtUtil.getRefreshExpiredMs());
        response.addCookie(cookie);

        String redirectUrl = REDIRECT_URL;
        response.sendRedirect(redirectUrl);
    }

    private Cookie createCookie(String key, String value, Long expiredMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiredMs.intValue());
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}

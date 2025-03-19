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

        String refreshToken = jwtUtil.createJwt(userDetails.getId().toString(), userDetails.getName(), jwtUtil.getRefreshTokenExpiredMs());
        Cookie refreshCookie = jwtUtil.createCookie(jwtUtil.getRefreshTokenName(), refreshToken, jwtUtil.getRefreshTokenExpiredMs());
        response.addCookie(refreshCookie);

        String accessToken = jwtUtil.createJwt(userDetails.getId().toString(), userDetails.getName(), jwtUtil.getAccessTokenExpiredMs());
        Cookie accessCookie = jwtUtil.createCookie(jwtUtil.getAccessTokenName(), accessToken, jwtUtil.getAccessTokenExpiredMs());
        response.addCookie(accessCookie);

        response.sendRedirect(REDIRECT_URL);
    }
}

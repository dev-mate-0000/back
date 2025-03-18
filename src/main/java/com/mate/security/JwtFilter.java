package com.mate.security;

import com.mate.member.domain.Member;
import com.mate.security.oauth.CustomOAuthUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessToken(request);
        if(accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Long id;
        String name;

        Claims accessClaims = jwtUtil.getClaims(accessToken);

        if(accessClaims == null) {
            String refreshToken = getRefreshToken(request);
            if(refreshToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Claims refreshClaims = jwtUtil.getClaims(refreshToken);
            if(refreshClaims == null) {
                filterChain.doFilter(request, response);
                return;
            }

            id = refreshClaims.get("id", Long.class);
            name = refreshClaims.get("name", String.class);

            String newRefreshToken = jwtUtil.createJwt(id, name, jwtUtil.getRefreshTokenExpiredMs());
            Cookie newRefreshCookie = jwtUtil.createCookie(jwtUtil.getRefreshTokenName(), newRefreshToken, jwtUtil.getRefreshTokenExpiredMs());
            response.addCookie(newRefreshCookie);

            String newAccessToken = jwtUtil.createJwt(id, name, jwtUtil.getAccessTokenExpiredMs());
            Cookie newAccessCookie = jwtUtil.createCookie(jwtUtil.getAccessTokenName(), newAccessToken, jwtUtil.getAccessTokenExpiredMs());
            response.addCookie(newAccessCookie);
        } else {
            id = accessClaims.get("id", Long.class);
            name = accessClaims.get("name", String.class);
        }

        Member member = Member.builder()
                .id(id)
                .name(name)
                .build();

        CustomOAuthUser customOAuthUser = new CustomOAuthUser(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuthUser, null, customOAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(jwtUtil.getAccessTokenName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(jwtUtil.getRefreshTokenName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

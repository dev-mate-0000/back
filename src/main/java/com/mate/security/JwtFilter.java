package com.mate.security;

import com.mate.member.domain.Member;
import com.mate.security.oauth.CustomOAuthUser;
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

        String token = getToken(request);
        if(token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if(jwtUtil.isExpired(token)) {
            // ToDo: 예외 처리.. 해야지..
            System.out.println("token expired");
            filterChain.doFilter(request, response);
            return;
        }

        Long id = jwtUtil.getId(token);
        String name = jwtUtil.getName(token);

        Member member = Member.builder()
                .id(id)
                .name(name)
                .build();

        CustomOAuthUser customOAuthUser = new CustomOAuthUser(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuthUser, null, customOAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(jwtUtil.getAccessTokenName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

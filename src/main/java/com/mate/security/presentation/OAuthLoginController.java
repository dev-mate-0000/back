package com.mate.security.presentation;

import com.mate.security.JwtUtil;
import com.mate.security.SecurityUtil;
import com.mate.security.application.OAuthLoginService;
import com.mate.security.oauth.CustomOAuthUser;
import com.mate.security.oauth.OAuthProvider;
import com.mate.security.oauth.oauthserver.GithubOAuthServer;
import com.mate.security.oauth.oauthserver.OAuthResponse;
import com.mate.security.presentation.dto.OAuthMemberResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthLoginController {

    private final OAuthLoginService oAuthLoginService;
    private final JwtUtil jwtUtil;

    private final GithubOAuthServer githubOAuthServer;

    @Value("${spring.security.redirectURL}")
    private String REDIRECT_URL;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    private final String GITHUB_LOGIN_URL = "https://github.com/login/oauth/authorize";

    private final String FAIL_GET_PROVIDER = "OAuth 공급자 정보를 찾을 수 없습니다.";

    @GetMapping("/github")
    public ResponseEntity<OAuthMemberResponse.OAuthUrl> getGithubLoginUrl() {
        Optional<CustomOAuthUser> userInfoOptional = SecurityUtil.getMemberIdByAuthentication();
        if(userInfoOptional.isEmpty()) {
            String url = GITHUB_LOGIN_URL + "?client_id=" + clientId;
            return ResponseEntity.ok()
                    .body(OAuthMemberResponse.OAuthUrl.toDto(url));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/code/github")
    public void githubCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        OAuthResponse oAuthResponse = getOAuthResponse(OAuthProvider.GITHUB);
        if(oAuthResponse == null) {
            throw new RuntimeException(FAIL_GET_PROVIDER);
        }
        oAuthResponse.setUserInfo(code);

        OAuthMemberResponse.OAuthFindMember member = oAuthLoginService.saveMember(oAuthResponse);
        onSuccess(member, response);
        response.sendRedirect(REDIRECT_URL);
    }

    private void onSuccess(OAuthMemberResponse.OAuthFindMember member, HttpServletResponse response) {
        String refreshToken = jwtUtil.createJwt(member.id().toString(), member.name(), jwtUtil.getRefreshTokenExpiredMs());
        Cookie refreshCookie = jwtUtil.createCookie(jwtUtil.getRefreshTokenName(), refreshToken, jwtUtil.getRefreshTokenExpiredMs());
        response.addCookie(refreshCookie);

        String accessToken = jwtUtil.createJwt(member.id().toString(), member.name(), jwtUtil.getAccessTokenExpiredMs());
        Cookie accessCookie = jwtUtil.createCookie(jwtUtil.getAccessTokenName(), accessToken, jwtUtil.getAccessTokenExpiredMs());
        response.addCookie(accessCookie);
    }

    private OAuthResponse getOAuthResponse(OAuthProvider oAuthProvider) {
        if(oAuthProvider.equals(OAuthProvider.GITHUB)) {
            return githubOAuthServer;
        }
        return null;
    }
}
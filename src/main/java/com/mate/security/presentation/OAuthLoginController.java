package com.mate.security.presentation;

import com.mate.security.JwtUtil;
import com.mate.security.application.OAuthLoginService;
import com.mate.security.oauth.oauthserver.GithubOAuthServer;
import com.mate.security.oauth.oauthserver.OAuthResponse;
import com.mate.security.presentation.dto.OAuthMemberResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthLoginController {

    private final OAuthLoginService oAuthLoginService;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;

    private final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private final String GITHUB_LOGIN_URL = "https://github.com/login/oauth/authorize";
    private final String GITHUB_USER_API_URL = "https://api.github.com/user";

    private final JwtUtil jwtUtil;

    @Value("${spring.security.redirectURL}")
    private String REDIRECT_URL;

    @GetMapping("/github")
    public ResponseEntity<String> getGithubLoginUrl() {
        return ResponseEntity.ok()
                .body(GITHUB_LOGIN_URL + "?client_id=" + clientId);
    }

    @GetMapping("/code/github")
    public void githubCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        String accessToken = getAccessToken(code);
        Map<String, Object> userInfo = getUserInfo(accessToken);

        if (userInfo == null) {
            throw new RuntimeException("GitHub 사용자 정보 가져오기 실패");
        }

        OAuthResponse oAuthResponse = getOAuthResponse("github", userInfo);
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

    private OAuthResponse getOAuthResponse(String provider, Map<String, Object> userInfo) {
        if(provider.equals("github")) {
            return new GithubOAuthServer(userInfo);
        }
        return null;
    }

    private String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(GITHUB_TOKEN_URL, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String[] responseBody = response.getBody().split("&");
            for (String param : responseBody) {
                if (param.startsWith("access_token=")) {
                    return param.split("=")[1];
                }
            }
        }
        throw new RuntimeException("GitHub 액세스 토큰 요청 실패");
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<Map> response = restTemplate.exchange(GITHUB_USER_API_URL, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
}
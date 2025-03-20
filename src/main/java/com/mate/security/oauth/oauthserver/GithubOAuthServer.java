package com.mate.security.oauth.oauthserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mate.security.oauth.OAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Component
public class GithubOAuthServer implements OAuthResponse{

    private Map<String, Object> response;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.githubToken}")
    private String githubToken;

    private final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private final String GITHUB_USER_API_URL = "https://api.github.com/user";
    private final String GITHUB_REPO_API_URL = "https://api.github.com/users";

    @Override
    public Integer getGithubId() {
        if(response.isEmpty()) return null;
        return (int) response.get("id");
    }

    @Override
    public String getGithubLogin() {
        if(response.isEmpty()) return null;
        return (String) response.get("login");
    }

    @Override
    public OAuthProvider getProvider() {
        if(response.isEmpty()) return null;
        return OAuthProvider.GITHUB;
    }

    @Override
    public String getName() {
        if(response.isEmpty()) return null;
        return response.get("name").toString();
    }

    @Override
    public String getGithubUrl() {
        if(response.isEmpty()) return null;
        return response.get("html_url").toString();
    }

    @Override
    public String getBio() {
        if(response.isEmpty()) return null;
        return Optional.ofNullable(response.get("bio"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    public String getEmail() {
        if(response.isEmpty()) return null;
        return Optional.ofNullable(response.get("email"))
                .map(Object::toString)
                .orElse("");
    }

    /**
     * 사용자의 사용 언어 정보를 불러옴
     * @param githubLogin github 로그인 정보
     * @return Map<String, Integer> (언어, 사용된 바이트 수)
     */
    @Override
    public Map<String, Integer> getLanguages(String githubLogin) {
        try {
            URL url = new URL(GITHUB_REPO_API_URL + "/" + githubLogin + "/repos");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setRequestProperty("Authorization", "token " + githubToken);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return Collections.emptyMap();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode reposNode = objectMapper.readTree(conn.getInputStream());
            conn.disconnect();

            Map<String, Integer> languageBytesMap = new HashMap<>();
            for (JsonNode repo : reposNode) {
                String languagesUrl = repo.get("languages_url").asText();
                Map<String, Integer> repoLanguages = fetchRepoLanguages(languagesUrl, githubToken);
                repoLanguages.forEach((lang, bytes) ->
                        languageBytesMap.merge(lang, bytes, Integer::sum)
                );
            }

            return languageBytesMap;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 특정 GitHub 저장소의 언어 사용량을 가져온다.
     * @param languagesUrl 저장소의 언어 정보를 제공하는 GitHub API URL
     * @return Map<String, Integer> (언어, 사용된 바이트 수)
     */
    private Map<String, Integer> fetchRepoLanguages(String languagesUrl, String githubToken) {
        try {
            URL url = new URL(languagesUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setRequestProperty("Authorization", "token " + githubToken);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return Collections.emptyMap();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode languagesNode = objectMapper.readTree(conn.getInputStream());
            conn.disconnect();

            Map<String, Integer> repoLanguages = new HashMap<>();
            languagesNode.fields().forEachRemaining(entry ->
                    repoLanguages.put(entry.getKey(), entry.getValue().asInt())
            );

            return repoLanguages;

        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    @Override
    public void setUserInfo(String code) {
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
                    String accessToken = param.split("=")[1];
                    this.setResponse(accessToken);
                }
            }
        }
    }

    private void setResponse(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<Map> response = restTemplate.exchange(GITHUB_USER_API_URL, HttpMethod.GET, entity, Map.class);

        this.response = response.getBody();
    }
}

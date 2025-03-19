package com.mate.security.oauth.oauthserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mate.security.oauth.OAuthServer;
import com.mate.security.oauth.OAuthUserService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class GithubOAuthServer implements OAuthResponse{

    private final Map<String, Object> res;

    private final String GITHUB_API_URL = "https://api.github.com/users/";

    public GithubOAuthServer(final Map<String, Object> res) {
        this.res = res;
    }

    @Override
    public Integer getGithubId() {
        return (int) res.get("id");
    }

    @Override
    public String getGithubLogin() {
        return (String) res.get("login");
    }

    @Override
    public OAuthServer getProvider() {
        return OAuthServer.GITHUB;
    }

    @Override
    public String getName() {
        return res.get("name").toString();
    }

    @Override
    public String getGithubUrl() {
        return res.get("html_url").toString();
    }

    @Override
    public String getBio() {
        return Optional.ofNullable(res.get("bio"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    public String getEmail() {
        return Optional.ofNullable(res.get("email"))
                .map(Object::toString)
                .orElse("");
    }

    @Override
    public Map<String, Integer> getLanguages(String githubToken) {
        String username = getGithubLogin();
        if (username == null) {
            return null;
        }

        try {
            URL url = new URL(GITHUB_API_URL + username + "/repos");
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
}

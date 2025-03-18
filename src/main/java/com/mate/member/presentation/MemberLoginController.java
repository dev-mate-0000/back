package com.mate.member.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class MemberLoginController {

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String GITHUB_CLIENT_ID;

    @GetMapping("/github")
    public ResponseEntity<String> getGithubLoginUrl() {
        return ResponseEntity.ok()
                .body("https://github.com/login/oauth/authorize?client_id=" + GITHUB_CLIENT_ID);
    }

    @GetMapping("/checker")
    public ResponseEntity<Void> checker() {
        return ResponseEntity.ok().build();
    }
}
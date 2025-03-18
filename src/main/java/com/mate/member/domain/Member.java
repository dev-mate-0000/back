package com.mate.member.domain;

import com.mate.security.oauth.OAuthServer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String githubUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthServer oAuthServer;

    @Builder.Default
    private Long priority = 0L;

    private String job;
    private String bio;
}

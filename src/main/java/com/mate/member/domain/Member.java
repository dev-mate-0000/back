package com.mate.member.domain;

import com.mate.member.presentation.enums.JobsEnum;
import com.mate.security.oauth.OAuthServer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private Integer githubId;

    @Column(nullable = false)
    private String githubLogin;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String githubUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthServer oAuthServer;

    @Builder.Default
    private Long priority = 0L;

    private JobsEnum job;
    private String bio;
    private String email;

    /**
     * Member Patch Method
     * @param job
     * @param bio
     */
    public void patchMember(JobsEnum job, String bio) {
        if(job != null) {
            this.job = job;
        }
        if(bio != null && !bio.isEmpty()) {
            this.bio = bio;
        }
    }
}

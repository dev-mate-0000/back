package com.mate.member.domain;

import com.mate.member.presentation.enums.JobsEnum;
import com.mate.member.presentation.enums.MemberStatusEnum;
import com.mate.security.oauth.OAuthProvider;
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
    private Integer providerId;

    @Column(nullable = false)
    private String providerLogin;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String providerUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    @Builder.Default
    private Long priority = 0L;

    @Enumerated(EnumType.STRING)
    private JobsEnum job;

    private String bio;

    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberStatusEnum status = MemberStatusEnum.PUBLIC;

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

    /**
     * Member Status Patch Method
     * @param status
     */
    public void patchStatus(MemberStatusEnum status) {
        this.status = status;
    }
}

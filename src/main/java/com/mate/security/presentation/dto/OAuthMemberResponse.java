package com.mate.security.presentation.dto;

import com.mate.member.domain.Member;
import lombok.Builder;

import java.util.UUID;

public class OAuthMemberResponse {
    @Builder
    public record OAuthFindMember(
        UUID id,
        String name
    ) {
        public static OAuthFindMember toDto(Member member) {
            return OAuthFindMember.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .build();
        }
    }

    @Builder
    public record OAuthUrl(
            String url
    ) {
        public static OAuthUrl toDto(String url) {
            return OAuthUrl.builder()
                    .url(url)
                    .build();
        }
    }
}

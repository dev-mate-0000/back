package com.mate.member.presentation.dto;

import com.mate.member.domain.Language;
import com.mate.member.domain.Member;
import lombok.Builder;

import java.util.List;

public class MemberResponse {

    @Builder
    public record FindMember(
        String name,
        String githubUrl,
        String job,
        String bio,
        List<FindMemberLanguageInfo> languages
    ) {
        @Builder
        private record FindMemberLanguageInfo(
                String language
        ) {}

        public static FindMember toDto(Member member, List<Language> languages) {
            List<FindMemberLanguageInfo> rtnLanguages = languages.stream().map(language ->
                    FindMemberLanguageInfo.builder()
                            .language(language.getLanguage())
                            .build()
            ).toList();

            return FindMember.builder()
                    .name(member.getName())
                    .githubUrl(member.getGithubUrl())
                    .job(member.getJob())
                    .bio(member.getBio())
                    .languages(rtnLanguages)
                    .build();
        }
    }
}

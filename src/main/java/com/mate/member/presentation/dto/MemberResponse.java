package com.mate.member.presentation.dto;

import com.mate.member.domain.Language;
import com.mate.member.domain.Member;
import com.mate.member.presentation.enums.JobsEnum;
import lombok.Builder;

import java.util.List;

public class MemberResponse {

    @Builder
    public record FindMember(
        Long id,
        String name,
        String githubUrl,
        JobsEnum job,
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
                            .language(String.valueOf(language.getLanguage()))
                            .build()
            ).toList();

            return FindMember.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .githubUrl(member.getGithubUrl())
                    .job(member.getJob())
                    .bio(member.getBio())
                    .languages(rtnLanguages)
                    .build();
        }
    }

    @Builder
    public record FindMemberSuggest(
            Long id,
            String name
    ) {

        public static FindMemberSuggest toDto(Member member) {
            return FindMemberSuggest.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .build();
        }
    }
}

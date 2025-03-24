package com.mate.member.presentation.dto;

import com.mate.member.domain.Skill;
import com.mate.member.domain.Member;
import com.mate.member.presentation.enums.JobsEnum;
import com.mate.member.presentation.enums.MemberStatusEnum;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class MemberResponse {

    @Builder
    public record FindMemberSelf(
            UUID id,
            String name,
            String githubUrl,
            JobsEnum job,
            String bio,
            MemberStatusEnum status,
            List<FindMemberLanguageInfo> languages
    ) {
        @Builder
        private record FindMemberLanguageInfo(
                String language
        ) {}

        public static FindMemberSelf toDto(Member member, List<Skill> languages) {
            List<FindMemberLanguageInfo> rtnSkills = languages.stream().map(skill ->
                    FindMemberLanguageInfo.builder()
                            .language(String.valueOf(skill.getLanguage()))
                            .build()
            ).toList();

            return FindMemberSelf.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .githubUrl(member.getProviderUrl())
                    .job(member.getJob())
                    .bio(member.getBio())
                    .status(member.getStatus())
                    .languages(rtnSkills)
                    .build();
        }
    }


    @Builder
    public record FindMember(
        UUID id,
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

        public static FindMember toDto(Member member, List<Skill> languages) {
            List<FindMemberLanguageInfo> rtnSkills = languages.stream().map(skill ->
                    FindMemberLanguageInfo.builder()
                            .language(String.valueOf(skill.getLanguage()))
                            .build()
            ).toList();

            return FindMember.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .githubUrl(member.getProviderUrl())
                    .job(member.getJob())
                    .bio(member.getBio())
                    .languages(rtnSkills)
                    .build();
        }
    }

    @Builder
    public record FindMemberSuggest(
            UUID id,
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

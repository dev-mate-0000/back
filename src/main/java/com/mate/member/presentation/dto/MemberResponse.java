package com.mate.member.presentation.dto;

import com.mate.member.domain.Skill;
import com.mate.member.domain.Member;
import com.mate.member.presentation.enums.JobsEnum;
import com.mate.member.presentation.enums.StatusEnum;
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
            StatusEnum status,
            List<FindMemberSelfLanguageInfo> languages
    ) {
        @Builder
        private record FindMemberSelfLanguageInfo(
                String language,
                Integer codes
        ) {}

        public static FindMemberSelf toDto(Member member, List<Skill> skills) {
            List<FindMemberSelfLanguageInfo> rtnSkills = skills.stream().map(skill ->
                    FindMemberSelfLanguageInfo.builder()
                            .language(String.valueOf(skill.getLanguage()))
                            .codes(skill.getCodes())
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
                String language,
                Integer codes
        ) {}

        public static FindMember toDto(Member member, List<Skill> skills) {
            List<FindMemberLanguageInfo> rtnSkills = skills.stream().map(skill ->
                    FindMemberLanguageInfo.builder()
                            .language(String.valueOf(skill.getLanguage()))
                            .codes(skill.getCodes())
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
}

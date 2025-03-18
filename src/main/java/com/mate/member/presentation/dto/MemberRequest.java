package com.mate.member.presentation.dto;

import com.mate.member.presentation.enums.JobsEnum;
import com.mate.member.presentation.enums.LanguagesEnum;
import lombok.Builder;

import java.util.List;

public class MemberRequest {
    @Builder
    public record PatchMember(
            JobsEnum job,
            String bio,
            List<LanguagesEnum> language
    ) {}
}

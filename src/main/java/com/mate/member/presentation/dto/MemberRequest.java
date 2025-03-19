package com.mate.member.presentation.dto;

import com.mate.member.presentation.enums.JobsEnum;
import lombok.Builder;

public class MemberRequest {
    @Builder
    public record PatchMember(
            JobsEnum job,
            String bio
    ) {}
}

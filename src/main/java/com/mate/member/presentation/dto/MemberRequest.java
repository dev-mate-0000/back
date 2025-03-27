package com.mate.member.presentation.dto;

import com.mate.member.presentation.enums.JobsEnum;
import com.mate.member.presentation.enums.StatusEnum;
import lombok.Builder;

public class MemberRequest {
    @Builder
    public record PatchMember(
            JobsEnum job,
            String bio,
            StatusEnum status
    ) {}
}

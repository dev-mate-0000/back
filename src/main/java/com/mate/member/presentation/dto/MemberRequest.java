package com.mate.member.presentation.dto;

import com.mate.member.presentation.enums.JobsEnum;
import com.mate.member.presentation.enums.StatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class MemberRequest {
    @Builder
    public record PatchMember(
            @NotNull
            JobsEnum job,

            @NotNull
            String bio,

            @NotNull
            StatusEnum status
    ) {}
}
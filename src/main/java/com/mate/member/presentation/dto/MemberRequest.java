package com.mate.member.presentation.dto;

import com.mate.member.presentation.enums.JobsEnum;
import com.mate.member.presentation.enums.MemberStatusEnum;
import jdk.jshell.Snippet;
import lombok.Builder;

public class MemberRequest {
    @Builder
    public record PatchMember(
            JobsEnum job,
            String bio,
            MemberStatusEnum status
    ) {}
}

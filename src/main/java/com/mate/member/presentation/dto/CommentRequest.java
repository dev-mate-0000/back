package com.mate.member.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

public class CommentRequest {
    @Builder
    public record SaveMember(
            @NotNull
            UUID memberId,

            @NotNull
            @Size(min = 1, max = 500)
            String review
    ) {}
}

package com.mate.member.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class CommentRequest {
    @Builder
    public record SaveComment(
            @NotNull
            @Size(min = 1, max = 500)
            String review
    ) {}

    @Builder
    public record PatchComment(
            @NotNull
            @Size(min = 1, max = 500)
            String review
    ) {}
}

package com.mate.member.presentation.dto;

import com.mate.member.domain.Comment;
import lombok.Builder;

import java.util.UUID;

public class CommentResponse {
    @Builder
    public record FindComment(
            FindCommentMemberInfo reviewerInfo,
            FindCommentMemberInfo memberInfo,
            String review
    ) {
        @Builder
        private record FindCommentMemberInfo(
                UUID id,
                String name
        ) {}

        public static CommentResponse.FindComment toDto(Comment comment) {
            FindCommentMemberInfo review = FindCommentMemberInfo.builder()
                    .id(comment.getReviewer().getId())
                    .name(comment.getReviewer().getName())
                    .build();

            FindCommentMemberInfo member = FindCommentMemberInfo.builder()
                    .id(comment.getMember().getId())
                    .name(comment.getMember().getName())
                    .build();

            return FindComment.builder()
                    .reviewerInfo(review)
                    .memberInfo(member)
                    .review(comment.getReview())
                    .build();
        }
    }
}

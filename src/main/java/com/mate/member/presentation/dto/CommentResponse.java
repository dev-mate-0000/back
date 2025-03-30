package com.mate.member.presentation.dto;

import com.mate.member.domain.Comment;
import lombok.Builder;

import java.util.UUID;

public class CommentResponse {
    @Builder
    public record FindComment(
            UUID id,
            String review,
            Boolean editable,
            FindCommentMemberInfo reviewerInfo,
            FindCommentMemberInfo memberInfo
    ) {
        @Builder
        private record FindCommentMemberInfo(
                UUID id,
                String name
        ) {}

        public static CommentResponse.FindComment toDto(UUID memberId, Comment comment) {
            FindCommentMemberInfo review = FindCommentMemberInfo.builder()
                    .id(comment.getReviewer().getId())
                    .name(comment.getReviewer().getName())
                    .build();

            FindCommentMemberInfo member = FindCommentMemberInfo.builder()
                    .id(comment.getMember().getId())
                    .name(comment.getMember().getName())
                    .build();

            boolean editable;

            if(comment.getReviewer().getId().equals(memberId)) {
                editable = true;
            } else {
                editable = false;
            }

            return FindComment.builder()
                    .id(comment.getId())
                    .reviewerInfo(review)
                    .memberInfo(member)
                    .review(comment.getReview())
                    .editable(editable)
                    .build();
        }
    }
}

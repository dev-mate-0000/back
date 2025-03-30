package com.mate.member.application;

import com.mate.config.exception.custom.NotFoundException;
import com.mate.member.domain.Comment;
import com.mate.member.domain.CommentRepository;
import com.mate.member.domain.Member;
import com.mate.member.domain.MemberRepository;
import com.mate.member.presentation.dto.CommentRequest;
import com.mate.member.presentation.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    private final String NOT_FOUND_MEMBER_EXCEPTION = "사용자 정보를 찾을 수 없습니다.";
    private final String NOT_FOUND_COMMENT_EXCEPTION = "댓글 정보를 찾을 수 없습니다.";
    private final String UNAUTHORIZED_COMMENT_MODIFICATION = "댓글을 수정할 권한이 없습니다.";

    @Transactional
    public void saveComment(UUID memberId, UUID targetId, CommentRequest.SaveComment dto) {
        Member reviewer = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));

        Member member = memberRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));

        Comment comment = Comment.builder()
                .reviewer(reviewer)
                .member(member)
                .review(dto.review())
                .build();

        commentRepository.save(comment);
    }

    /**
     * 특정 유저의 댓글 목록 조회
     * @param memberId 호출하는 유저 ID
     * @param targetMemberId 댓글 목록을 찾고자 하는 유저 ID
     * @return
     */
    public List<CommentResponse.FindComment> findCommentById(UUID memberId, UUID targetMemberId) {
        List<Comment> comments = commentRepository.findByMemberId(targetMemberId);
        return comments.stream().map(comment -> CommentResponse.FindComment.toDto(memberId, comment)).toList();
    }

    /**
     * 댓글 수정
     * @param commentId 댓글 ID
     * @param reviewerId 작성자 유저 ID(호출하는 유저 ID) 실제 작성자와 다르다면 NotFoundException
     * @param dto
     */
    @Transactional
    public void patchCommentByCommentIdAndReviewerId(UUID commentId, UUID reviewerId, CommentRequest.PatchComment dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_COMMENT_EXCEPTION));
        if(!comment.getReviewer().getId().equals(reviewerId)) {
            throw new NotFoundException(UNAUTHORIZED_COMMENT_MODIFICATION);
        }
        comment.patch(dto.review());
    }

    /**
     * 댓글 삭제
     * @param commentId 댓글 ID
     * @param reviewerId 작성자 유저 ID(호출하는 유저 ID) 실제 작성자와 다르다면 NotFoundException
     */
    @Transactional
    public void deleteCommentByCommentIdAndReviewId(UUID commentId, UUID reviewerId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_COMMENT_EXCEPTION));
        if(!comment.getReviewer().getId().equals(reviewerId)) {
            throw new NotFoundException(UNAUTHORIZED_COMMENT_MODIFICATION);
        }
        commentRepository.delete(comment);
    }
}

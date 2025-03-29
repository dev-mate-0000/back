package com.mate.member.application;

import com.mate.config.exception.custom.NotFoundException;
import com.mate.member.domain.Comment;
import com.mate.member.domain.CommentRepository;
import com.mate.member.domain.Member;
import com.mate.member.domain.MemberRepository;
import com.mate.member.presentation.dto.CommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    private final String NOT_FOUND_MEMBER_EXCEPTION = "사용자 정보를 찾을 수 없습니다.";

    @Transactional
    public void save(UUID memberId, CommentRequest.SaveMember dto) {
        Member reviewer = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));

        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));

        Comment comment = Comment.builder()
                .reviewer(reviewer)
                .member(member)
                .review(dto.review())
                .build();

        commentRepository.save(comment);
    }
}

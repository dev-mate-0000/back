package com.mate.member.presentation;

import com.mate.config.exception.custom.AuthedException;
import com.mate.member.application.CommentService;
import com.mate.member.presentation.dto.CommentRequest;
import com.mate.member.presentation.dto.CommentResponse;
import com.mate.member.presentation.dto.MemberResponse;
import com.mate.security.SecurityUtil;
import com.mate.security.oauth.CustomOAuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/members/comments")
@RequiredArgsConstructor
public class CommentController {

    public final CommentService commentService;

    private static final String UNAUTHORIZED_USER = "인증되지 않은 사용자입니다";

    @GetMapping()
    public ResponseEntity<Void> findMemberById(@Valid CommentRequest.SaveMember dto) {
        CustomOAuthUser userInfo = SecurityUtil.getMemberIdByAuthentication()
                .orElseThrow(() -> new AuthedException(UNAUTHORIZED_USER));
        commentService.save(userInfo.getId(), dto);
        return ResponseEntity.ok().build();
    }
}

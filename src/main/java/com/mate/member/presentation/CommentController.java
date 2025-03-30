package com.mate.member.presentation;

import com.mate.config.exception.custom.AuthedException;
import com.mate.member.application.CommentService;
import com.mate.member.presentation.dto.CommentRequest;
import com.mate.member.presentation.dto.CommentResponse;
import com.mate.security.SecurityUtil;
import com.mate.security.oauth.CustomOAuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/members/comments")
@RequiredArgsConstructor
public class CommentController {

    public final CommentService commentService;

    private static final String UNAUTHORIZED_USER = "인증되지 않은 사용자입니다";

    @PostMapping("/{id}")
    public ResponseEntity<Void> saveCommentBymemberId(@PathVariable("id") UUID targetId, @Valid @RequestBody CommentRequest.SaveComment dto) {
        CustomOAuthUser userInfo = SecurityUtil.getMemberIdByAuthentication()
                .orElseThrow(() -> new AuthedException(UNAUTHORIZED_USER));
        commentService.saveComment(userInfo.getId(), targetId, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> patchCommentById(@PathVariable("commentId") UUID commentId, @Valid @RequestBody CommentRequest.PatchComment dto) {
        CustomOAuthUser userInfo = SecurityUtil.getMemberIdByAuthentication()
                .orElseThrow(() -> new AuthedException(UNAUTHORIZED_USER));
        commentService.patchCommentByCommentIdAndReviewerId(commentId, userInfo.getId(), dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> DeleteCommentById(@PathVariable("commentId") UUID commentId) {
        CustomOAuthUser userInfo = SecurityUtil.getMemberIdByAuthentication()
                .orElseThrow(() -> new AuthedException(UNAUTHORIZED_USER));
        commentService.deleteCommentByCommentIdAndReviewId(commentId, userInfo.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/self")
    public ResponseEntity<List<CommentResponse.FindComment>> findCommentByLoggedInUser() {
        CustomOAuthUser userInfo = SecurityUtil.getMemberIdByAuthentication()
                .orElseThrow(() -> new AuthedException(UNAUTHORIZED_USER));
        return ResponseEntity.ok().body(commentService.findCommentById(userInfo.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<CommentResponse.FindComment>> findCommentById(@PathVariable("id") UUID targetId) {
        return ResponseEntity.ok().body(commentService.findCommentById(targetId));
    }
}

package com.mate.member.presentation;

import com.mate.config.exception.custom.AuthedException;
import com.mate.member.application.MemberService;
import com.mate.member.presentation.dto.MemberRequest;
import com.mate.member.presentation.dto.MemberResponse;
import com.mate.security.JwtUtil;
import com.mate.security.SecurityUtil;
import com.mate.security.oauth.CustomOAuthUser;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    private static final String UNAUTHORIZED_USER = "인증되지 않은 사용자입니다";

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse.FindMember> findMemberById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok().body(memberService.findMemberById(id));
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<MemberResponse.FindMember>> suggestMember(@RequestParam("page") int page) {
        return ResponseEntity.ok().body(memberService.suggestMembers(page));
    }

    @GetMapping("/self")
    public ResponseEntity<MemberResponse.FindMemberSelf> findMemberByLoggedInUser() {
        CustomOAuthUser userInfo = SecurityUtil.getMemberIdByAuthentication()
                .orElseThrow(() -> new AuthedException(UNAUTHORIZED_USER));
        return ResponseEntity.ok().body(memberService.findMemberBySelf(userInfo.getId()));
    }

    @PatchMapping("/self")
    public ResponseEntity<Void> patchMemberByLoggedInUser(@RequestBody @Valid MemberRequest.PatchMember dto) {
        CustomOAuthUser userInfo = SecurityUtil.getMemberIdByAuthentication()
                .orElseThrow(() -> new AuthedException(UNAUTHORIZED_USER));
        memberService.patchMemberBySelf(userInfo.getId(), dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/self")
    public ResponseEntity<Void> deleteMemberByLoggedInUser(HttpServletResponse response) {
        CustomOAuthUser userInfo = SecurityUtil.getMemberIdByAuthentication()
                .orElseThrow(() -> new AuthedException(UNAUTHORIZED_USER));
        memberService.deleteMemberBySelf(userInfo.getId());

        ResponseCookie deleteCookieAccess = ResponseCookie.from(jwtUtil.getAccessTokenName(), "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookieAccess.toString());

        ResponseCookie deleteCookieRefresh = ResponseCookie.from(jwtUtil.getRefreshTokenName(), "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookieRefresh.toString());

        return ResponseEntity.noContent().build();
    }
}

package com.mate.member.presentation;

import com.mate.member.application.MemberService;
import com.mate.member.presentation.dto.MemberResponse;
import com.mate.security.SecurityUtil;
import com.mate.security.oauth.CustomOAuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/self")
    public MemberResponse.FindMember findMemberById() {
        CustomOAuthUser authUser = SecurityUtil.getMemberIdByAuthentication();
        return memberService.findMemberById(authUser.getId());
    }
}

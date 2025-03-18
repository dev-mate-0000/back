package com.mate.member.presentation;

import com.mate.member.application.MemberService;
import com.mate.member.presentation.dto.MemberResponse;
import com.mate.security.SecurityUtil;
import com.mate.security.oauth.CustomOAuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse.FindMember> findMemberById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(memberService.findMemberById(id));
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<MemberResponse.FindMemberSuggest>> suggestMember() {
        return ResponseEntity.ok().body(memberService.suggestMembers());
    }

    @GetMapping("/suggest/next")
    public ResponseEntity<MemberResponse.FindMemberSuggest> suggestNextMember(@RequestParam("page") int page) {
        return ResponseEntity.ok().body(memberService.suggestNextMember(page));
    }
}

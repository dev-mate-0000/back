package com.mate.member.application;

import com.mate.config.exception.custom.NotFoundException;
import com.mate.member.domain.Skill;
import com.mate.member.domain.SkillRepository;
import com.mate.member.domain.Member;
import com.mate.member.domain.MemberRepository;
import com.mate.member.presentation.dto.MemberRequest;
import com.mate.member.presentation.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final SkillRepository skillRepository;

    private final String NOT_FOUND_MEMBER_EXCEPTION = "사용자 정보를 찾을 수 없습니다.";

    public MemberResponse.FindMember findMemberById(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));
        List<Skill> skills = skillRepository.findByMemberId(member.getId());
        return MemberResponse.FindMember.toDto(member, skills);
    }

    public List<MemberResponse.FindMember> suggestMembers(int page) {
        List<Member> members = memberRepository.findSuggestMembers(page);
        return members.stream().map(member -> {
            List<Skill> skills = skillRepository.findByMemberId(member.getId());
            return MemberResponse.FindMember.toDto(member, skills);
        }).toList();
    }

    public MemberResponse.FindMemberSelf findMemberBySelf(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));
        List<Skill> skills = skillRepository.findByMemberId(member.getId());
        return MemberResponse.FindMemberSelf.toDto(member, skills);
    }

    @Transactional
    public void patchMemberBySelf(UUID memberId, MemberRequest.PatchMember dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));
        member.patchMember(dto.job(), dto.bio(), dto.status());
    }
}

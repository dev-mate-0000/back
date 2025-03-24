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

    public List<MemberResponse.FindMemberSuggest> suggestMembers() {
        List<Member> members = memberRepository.findSuggestMembers();
        return members.stream().map(MemberResponse.FindMemberSuggest::toDto).toList();
    }

    public MemberResponse.FindMemberSuggest suggestNextMember(int page) {
        Member member = memberRepository.findSuggestNextMember(page)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));
        return MemberResponse.FindMemberSuggest.toDto(member);
    }

    @Transactional
    public MemberResponse.FindMemberSelf patchMember(UUID memberId, MemberRequest.PatchMember dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));
        member.patchMember(dto.job(), dto.bio());

        List<Skill> skills = skillRepository.findByMemberId(member.getId());
        return MemberResponse.FindMemberSelf.toDto(member, skills);
    }

    @Transactional
    public MemberResponse.FindMemberSelf patchMemberStatus(UUID memberId, MemberRequest.PatchMemberStatus dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));
        member.patchStatus(dto.status());
        List<Skill> skills = skillRepository.findByMemberId(member.getId());
        return MemberResponse.FindMemberSelf.toDto(member, skills);
    }
}

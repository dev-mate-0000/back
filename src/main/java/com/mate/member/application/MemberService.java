package com.mate.member.application;

import com.mate.config.exception.custom.NotFoundException;
import com.mate.member.domain.Language;
import com.mate.member.domain.LanguageRepository;
import com.mate.member.domain.Member;
import com.mate.member.domain.MemberRepository;
import com.mate.member.presentation.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LanguageRepository languageRepository;

    private final String NOT_FOUND_MEMBER_EXCEPTION = "사용자 정보를 찾을 수 없습니다.";

    public MemberResponse.FindMember findMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER_EXCEPTION));
        List<Language> languages = languageRepository.findByMemberId(member.getId());
        return MemberResponse.FindMember.toDto(member, languages);
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
}

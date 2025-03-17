package com.mate.member.application;

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

    public MemberResponse.FindMember findMemberById(Long id) {
        Member member = memberRepository.findById(id);
        List<Language> languages = languageRepository.findByMemberId(member.getId());
        return MemberResponse.FindMember.toDto(member, languages);
    }
}

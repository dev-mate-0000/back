package com.mate.security.application;

import com.mate.member.domain.Language;
import com.mate.member.domain.LanguageRepository;
import com.mate.member.domain.Member;
import com.mate.member.domain.MemberRepository;
import com.mate.security.oauth.oauthserver.OAuthResponse;
import com.mate.security.presentation.dto.OAuthMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final MemberRepository memberRepository;
    private final LanguageRepository languageRepository;

    public OAuthMemberResponse.OAuthFindMember saveMember(OAuthResponse oAuthResponse) {
        Member member = getMember(oAuthResponse);

        Map<String, Integer> languages = oAuthResponse.getLanguages(oAuthResponse.getGithubLogin());
        languages.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(5)
                .forEach(data -> {
                    Language language = Language.builder()
                            .language(data.getKey())
                            .codeLines(data.getValue())
                            .member(member)
                            .build();

                    languageRepository.save(language);
                });
        return OAuthMemberResponse.OAuthFindMember.toDto(member);
    }

    private Member getMember(OAuthResponse oAuthResponse) {
        Member member = memberRepository.findByGithubId(oAuthResponse.getGithubId())
                .orElse(Member.builder()
                        .githubId(oAuthResponse.getGithubId())
                        .githubLogin(oAuthResponse.getGithubLogin())
                        .name(oAuthResponse.getName())
                        .githubUrl(oAuthResponse.getGithubUrl())
                        .bio(oAuthResponse.getBio())
                        .email(oAuthResponse.getEmail())
                        .oAuthProvider(oAuthResponse.getProvider())
                        .build());

        if(member.getId() != null) {
            languageRepository.deleteByMemberId(member.getId());
        }

        return memberRepository.save(member);
    }
}

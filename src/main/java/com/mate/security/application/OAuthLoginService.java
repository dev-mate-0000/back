package com.mate.security.application;

import com.mate.member.domain.Skill;
import com.mate.member.domain.SkillRepository;
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
    private final SkillRepository skillRepository;

    public OAuthMemberResponse.OAuthFindMember saveMember(OAuthResponse oAuthResponse) {
        Member member = getMember(oAuthResponse);

        Map<String, Integer> skills = oAuthResponse.getSkills(oAuthResponse.getGithubLogin());
        skills.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(5)
                .forEach(data -> {
                    Skill skill = Skill.builder()
                            .language(data.getKey())
                            .codes(data.getValue())
                            .member(member)
                            .build();

                    skillRepository.save(skill);
                });
        return OAuthMemberResponse.OAuthFindMember.toDto(member);
    }

    private Member getMember(OAuthResponse oAuthResponse) {
        Member member = memberRepository.findByGithubId(oAuthResponse.getGithubId())
                .orElse(Member.builder()
                        .providerId(oAuthResponse.getGithubId())
                        .providerLogin(oAuthResponse.getGithubLogin())
                        .name(oAuthResponse.getName())
                        .providerUrl(oAuthResponse.getGithubUrl())
                        .bio(oAuthResponse.getBio())
                        .email(oAuthResponse.getEmail())
                        .oAuthProvider(oAuthResponse.getProvider())
                        .build());

        if(member.getId() != null) {
            skillRepository.deleteByMemberId(member.getId());
        }

        return memberRepository.save(member);
    }
}

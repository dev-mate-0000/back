package com.mate.security.oauth;

import com.mate.config.exception.custom.AuthedException;
import com.mate.member.domain.Language;
import com.mate.member.domain.LanguageRepository;
import com.mate.member.domain.Member;
import com.mate.member.domain.MemberRepository;
import com.mate.security.oauth.oauthserver.GithubOAuthServer;
import com.mate.security.oauth.oauthserver.OAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthUserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final LanguageRepository languageRepository;

    @Value("${spring.security.githubToken}")
    private String githubToken;

    private final String AUTH_FAILURE = "사용자 인증에 실패했습니다.";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuthResponse oAuthResponse = getOAuthResponse(userRequest, oAuth2User);
        if(oAuthResponse == null) {
            throw new AuthedException(AUTH_FAILURE);
        }

        Member member = getMember(oAuthResponse);

        Map<String, Integer> languages = oAuthResponse.getLanguages(githubToken);
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

        return new CustomOAuthUser(member);
    }

    private OAuthResponse getOAuthResponse(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if(registrationId.equals("github")) {
            return new GithubOAuthServer(oAuth2User.getAttributes());
        }
        return null;
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
                    .oAuthServer(oAuthResponse.getProvider())
                    .build());

        if(member.getId() != null) {
            languageRepository.deleteByMemberId(member.getId());
        }

        return memberRepository.save(member);
    }
}

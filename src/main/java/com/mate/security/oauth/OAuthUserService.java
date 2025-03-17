package com.mate.security.oauth;

import com.mate.member.domain.Member;
import com.mate.member.domain.MemberRepository;
import com.mate.security.oauth.oauthserver.GithubOAuthServer;
import com.mate.security.oauth.oauthserver.OAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class OAuthUserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("loadUser method called");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuthResponse oAuthResponse = getOAuthResponse(userRequest, oAuth2User);
        Assert.notNull(oAuthResponse, "E");

        Member member = Member.builder()
                .name(oAuthResponse.getName())
                .githubUrl(oAuthResponse.getGithubUrl())
                .oAuthServer(oAuthResponse.getProvider())
                .build();

        Member savedMember = memberRepository.save(member);
        return new CustomOAuthUser(savedMember);
    }

    private OAuthResponse getOAuthResponse(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if(registrationId.equals("github")) {
            return new GithubOAuthServer(oAuth2User.getAttributes());
        }
        return null;
    }
}

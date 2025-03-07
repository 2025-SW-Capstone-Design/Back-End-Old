package soon.capstone.global.oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.external.github.service.GithubEmailService;
import soon.capstone.global.exception.member.MemberNotFoundException;
import soon.capstone.global.oauth2.dto.CustomOAuth2Member;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class Oauth2GithubService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final GithubEmailService githubEmailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oauth2Token = userRequest.getAccessToken().getTokenValue();
        Member member = findOrCreateMember(oAuth2User, oauth2Token);
        log.info("Oauth2GithubService.loadUser oauth2Token: {}", oauth2Token);

        return new CustomOAuth2Member(
            Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
            oAuth2User.getAttributes(),
            "id",
            member.getId(),
            member.getNickname(),
            oauth2Token
        );
    }

    private Member findOrCreateMember(OAuth2User oAuth2User, String oauth2Token) {
        String nickname = oAuth2User.getName();
        try {
            return memberRepository.findByNickname(nickname);
        } catch (MemberNotFoundException e) {
            Member member = createMember(oAuth2User, oauth2Token, nickname);
            memberRepository.save(member);
            return member;
        }
    }

    private Member createMember(OAuth2User oAuth2User, String oauth2Token, String nickname) {
        String profileImageURL = oAuth2User.getAttribute("avatar_url");
        String email = getEmail(oAuth2User.getAttribute("email"), oauth2Token);
        return Member.builder()
            .email(email)
            .nickname(nickname)
            .profileImageURL(profileImageURL)
            .build();
    }

    private String getEmail(String email, String oauth2Token) {
        if (email == null) {
            return githubEmailService.fetchPrimaryVerifiedEmail(oauth2Token);
        }

        return email;
    }

}
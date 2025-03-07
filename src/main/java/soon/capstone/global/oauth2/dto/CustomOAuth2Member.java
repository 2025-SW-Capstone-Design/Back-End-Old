package soon.capstone.global.oauth2.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2Member extends DefaultOAuth2User {

    private final Long memberId;
    private final String nickname;
    private final String oauth2Token;

    public CustomOAuth2Member(
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> attributes,
        String nameAttributeKey,
        Long memberId,
        String nickname,
        String oauth2Token
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.memberId = memberId;
        this.nickname = nickname;
        this.oauth2Token = oauth2Token;
    }

}
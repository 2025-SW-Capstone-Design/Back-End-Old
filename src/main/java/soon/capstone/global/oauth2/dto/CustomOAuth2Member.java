package soon.capstone.global.oauth2.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2Member extends DefaultOAuth2User {

    private final String nickname;

    public CustomOAuth2Member(
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> attributes,
        String nameAttributeKey,
        String nickname
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.nickname = nickname;
    }

}
package soon.capstone;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import soon.capstone.global.anootation.TestMember;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestSecurityContext implements WithSecurityContextFactory<TestMember> {

    @Override
    public SecurityContext createSecurityContext(TestMember annotation) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        List<SimpleGrantedAuthority> authorities = Arrays.stream(annotation.roles())
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());

        String id = String.valueOf(annotation.id());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(id, null, authorities);
        securityContext.setAuthentication(authentication);
        return securityContext;
    }

}
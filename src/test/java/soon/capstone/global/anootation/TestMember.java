package soon.capstone.global.anootation;

import org.springframework.security.test.context.support.WithSecurityContext;
import soon.capstone.TestSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestSecurityContext.class)
public @interface TestMember {

    long id() default 1L;

    String[] roles() default {"USER"};
}
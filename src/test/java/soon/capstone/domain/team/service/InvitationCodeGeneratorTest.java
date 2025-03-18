package soon.capstone.domain.team.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InvitationCodeGeneratorTest {

    private final InvitationCodeGenerator invitationCodeGenerator = new InvitationCodeGenerator();

    @DisplayName("초대 코드 생성 시 중복 코드는 생성되지 않는다.")
    @Test
    void generateUniqueInvitationCodes() {
        // given
        int sampleSize = 1000000;
        Set<String> generatedCodes = new HashSet<>();

        // when
        for (int i = 0; i < sampleSize; i++) {
            String code = invitationCodeGenerator.generateInvitationCode();
            generatedCodes.add(code);
        }

        // then
        assertThat(generatedCodes).hasSize(sampleSize);
    }

    @DisplayName("초대 코드는 8자리의 문자열로 생성된다.")
    @Test
    void generateInvitationCodeHasLengthIsEight() {
        // given
        String code = invitationCodeGenerator.generateInvitationCode();

        // expect
        assertThat(code)
            .isNotNull()
            .hasSize(8);
    }

    @DisplayName("생성된 초대 코드는 영어와 숫자로만 구성된다.")
    @Test
    void generateInvitationCodeIsAlphabetAndNumber() {
        // given
        String code = invitationCodeGenerator.generateInvitationCode();

        // expected
        assertThat(code)
            .isNotNull()
            .matches("^[a-zA-Z0-9]*$");
    }

}
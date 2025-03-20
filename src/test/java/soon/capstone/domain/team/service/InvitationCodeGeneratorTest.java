package soon.capstone.domain.team.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soon.capstone.infrastructure.redis.invitation.entity.InvitationCode;
import soon.capstone.infrastructure.redis.invitation.repository.InvitationCodeRepository;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvitationCodeGeneratorTest {

    private static final long TEAM_ID = 1L;

    private InvitationCodeGenerator invitationCodeGenerator;

    @Mock
    private InvitationCodeRepository invitationCodeRepository;

    @BeforeEach
    void setUp() {
        invitationCodeGenerator = new InvitationCodeGenerator(invitationCodeRepository);
    }

    @DisplayName("초대 코드 생성 시 중복 코드는 생성되지 않는다")
    @Test
    void generateUniqueInvitationCodes() {
        // given
        int sampleSize = 100000;
        Set<String> generatedCodes = new HashSet<>();

        // when
        for (int i = 0; i < sampleSize; i++) {
            generatedCodes.add(invitationCodeGenerator.generateInvitationCode(TEAM_ID));
        }

        // then
        assertThat(generatedCodes).hasSize(sampleSize);
    }

    @DisplayName("초대 코드는 8자리의 문자열로 생성된다")
    @Test
    void validateInvitationCodeLength() {
        // given
        String code = invitationCodeGenerator.generateInvitationCode(TEAM_ID);

        // then
        assertThat(code).hasSize(8);
    }

    @DisplayName("생성된 초대 코드는 영어와 숫자로만 구성된다")
    @Test
    void validateInvitationCodeFormat() {
        // given
        String code = invitationCodeGenerator.generateInvitationCode(TEAM_ID);

        // then
        assertThat(code).matches("^[a-zA-Z0-9]*$");
    }

    @DisplayName("생성된 코드는 Redis에 저장된다")
    @Test
    void saveCodeToRedis() {
        // given
        String code = invitationCodeGenerator.generateInvitationCode(TEAM_ID);
        ArgumentCaptor<InvitationCode> captor = ArgumentCaptor.forClass(InvitationCode.class);

        // when
        verify(invitationCodeRepository).save(captor.capture());

        // then
        InvitationCode savedCode = captor.getValue();
        assertThat(savedCode)
            .extracting("code", "teamId")
            .contains(code, TEAM_ID);
    }

}
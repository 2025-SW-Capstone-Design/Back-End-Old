package soon.capstone.domain.team.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.global.email.service.EmailSendService;
import soon.capstone.infrastructure.redis.invitation.entity.InvitationCode;
import soon.capstone.infrastructure.redis.invitation.repository.InvitationCodeRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class TeamInvitationServiceTest extends IntegrationTestSupport {

    @Autowired
    private TeamInvitationService teamInvitationService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InvitationCodeRepository invitationCodeRepository;

    @MockitoBean
    private InvitationCodeGenerator invitationCodeGenerator;

    @MockitoBean
    private EmailSendService emailSendService;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        invitationCodeRepository.deleteAll();
    }

    @DisplayName("팀 초대 코드를 생성한다")
    @Test
    void generateInvitationCode() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        String fixedCode = "ABCD123";

        given(invitationCodeGenerator.generateInvitationCode(team.getId()))
            .willReturn(fixedCode);

        // when
        String invitationCode = teamInvitationService.generateInvitationCode(team.getId());

        // then
        assertThat(invitationCode).isEqualTo(fixedCode);
    }

    @DisplayName("입력된 이메일들에 초대 코드를 전송한다")
    @Test
    void sendInvitationEmails() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        String fixedCode = "ABCD123";

        InvitationCode invitationCode = InvitationCode.builder()
            .code(fixedCode)
            .teamId(team.getId())
            .build();
        invitationCodeRepository.save(invitationCode);

        List<String> emails = List.of("test1@example.com", "test2@example.com");

        // when
        teamInvitationService.sendInvitationEmails(team.getId(), emails);

        // then
        then(emailSendService)
            .should(times(2))
            .sendInvitationCodeEmail(anyString(), eq(fixedCode));
    }

    private Team createTeam() {
        return Team.builder()
            .name("name")
            .description("description")
            .organizationName("organizationName")
            .build();
    }

}
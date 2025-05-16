package soon.capstone.domain.meetinglog.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.meetinglog.entity.MeetingLog;
import soon.capstone.domain.meetinglog.repository.MeetingLogRepository;
import soon.capstone.domain.meetinglog.service.dto.request.MeetingLogCreateServiceRequest;
import soon.capstone.domain.meetinglog.service.dto.request.MeetingLogUpdateServiceRequest;
import soon.capstone.domain.meetinglog.service.dto.response.MeetingLogDetailResponse;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class MeetingLogServiceTest extends IntegrationTestSupport {

    @Autowired
    private MeetingLogService meetingLogService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private MeetingLogRepository meetingLogRepository;

    @AfterEach
    void tearDown() {
        meetingLogRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회의록을 생성 할 때 제목은 팀이름과 현재날짜로 생성된다.")
    @Test
    void create() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        MeetingLogCreateServiceRequest request = MeetingLogCreateServiceRequest.builder()
            .memberId(member.getId())
            .teamId(team.getId())
            .content("content")
            .build();

        String title = "회의록 - " + team.getName() + " (" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + ")";

        // when
        meetingLogService.create(request);

        // then
        assertThat(meetingLogRepository.findAll()).hasSize(1)
            .extracting("title", "content")
            .contains(tuple(title, "content"));
    }

    @DisplayName("회의록을 수정한다.")
    @Test
    void update() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        MeetingLog meetingLog = MeetingLog.create("content", member, team, LocalDate.now());
        meetingLogRepository.save(meetingLog);

        MeetingLogUpdateServiceRequest request = MeetingLogUpdateServiceRequest.builder()
            .id(meetingLog.getId())
            .teamId(team.getId())
            .memberId(member.getId())
            .content("new content")
            .title("new title")
            .build();

        // when
        meetingLogService.update(request);

        // then
        MeetingLog updatedMeetingLog = meetingLogRepository.findById(meetingLog.getId());
        assertThat(updatedMeetingLog)
            .extracting("title", "content")
            .contains("new title", "new content");
    }

    @DisplayName("회의록을 조회한다.")
    @Test
    void getMeetingLogDetail() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        MeetingLog meetingLog = MeetingLog.create("content", member, team, LocalDate.now());
        meetingLogRepository.save(meetingLog);

        // when
        MeetingLogDetailResponse response = meetingLogService.getMeetingLogDetail(meetingLog.getId());

        // then
        assertThat(response)
            .extracting("id", "title", "content", "createdAt")
            .contains(meetingLog.getId(), meetingLog.getTitle(), meetingLog.getContent(), meetingLog.getCreateTime().toLocalDate());
    }

    @DisplayName("팀에 해당하는 모든 회의록을 날짜의 내림차순으로 조회한다.")
    @Test
    void getMeetingLogsByTeamId() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        MeetingLog meetingLog1 = MeetingLog.create("content1", member, team, LocalDate.now());
        MeetingLog meetingLog2 = MeetingLog.create("content2", member, team, LocalDate.now().minusDays(1));
        meetingLogRepository.saveAll(List.of(meetingLog1, meetingLog2));

        // when
        List<MeetingLogDetailResponse> responses = meetingLogService.getMeetingLogsByTeamId(team.getId());

        // then
        assertThat(responses).hasSize(2)
            .extracting("id", "title", "content", "createdAt")
            .containsExactlyInAnyOrder(
                tuple(meetingLog1.getId(), meetingLog1.getTitle(), meetingLog1.getContent(), meetingLog1.getCreateTime().toLocalDate()),
                tuple(meetingLog2.getId(), meetingLog2.getTitle(), meetingLog2.getContent(), meetingLog2.getCreateTime().toLocalDate())
            );
    }

    private Member createMember(String email, String nickname) {
        return Member.builder()
            .email(email)
            .nickname(nickname)
            .profileImageURL("profileImageURL")
            .build();
    }

    private Team createTeam() {
        return Team.builder()
            .organizationName("organizationName")
            .name("teamName")
            .description("teamDescription")
            .build();
    }

}
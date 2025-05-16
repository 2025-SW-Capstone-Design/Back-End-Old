package soon.capstone.domain.meetinglog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.meetinglog.entity.MeetingLog;
import soon.capstone.domain.meetinglog.repository.MeetingLogRepository;
import soon.capstone.domain.meetinglog.service.dto.request.MeetingLogCreateServiceRequest;
import soon.capstone.domain.meetinglog.service.dto.request.MeetingLogUpdateServiceRequest;
import soon.capstone.domain.meetinglog.service.dto.response.MeetingLogDetailResponse;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.service.TeamMemberValidator;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MeetingLogService {

    private final MeetingLogRepository meetingLogRepository;
    private final TeamMemberValidator teamMemberValidator;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public void create(MeetingLogCreateServiceRequest request) {
        Member member = memberRepository.findById(request.memberId());
        Team team = teamRepository.findById(request.teamId());

        meetingLogRepository.save(MeetingLog.create(request.content(), member, team, LocalDate.now()));
    }

    @Transactional
    public void update(MeetingLogUpdateServiceRequest request) {
        teamMemberValidator.validateTeamMember(request.teamId(), request.memberId());

        MeetingLog meetingLog = meetingLogRepository.findById(request.id());
        meetingLog.update(request.title(), request.content());
    }

    public MeetingLogDetailResponse getMeetingLogDetail(Long meetingLogId) {
        MeetingLog meetingLog = meetingLogRepository.findById(meetingLogId);
        return MeetingLogDetailResponse.from(meetingLog);
    }

    public List<MeetingLogDetailResponse> getMeetingLogsByTeamId(Long teamId) {
        List<MeetingLog> meetingLogs = meetingLogRepository.findAllByTeamIdOrderByCreateTimeDesc(teamId);
        return meetingLogs.stream()
            .map(MeetingLogDetailResponse::from)
            .toList();
    }

}
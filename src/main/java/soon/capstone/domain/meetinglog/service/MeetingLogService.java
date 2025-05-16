package soon.capstone.domain.meetinglog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.meetinglog.entity.MeetingLog;
import soon.capstone.domain.meetinglog.repository.MeetingLogRepository;
import soon.capstone.domain.meetinglog.service.dto.request.MeetingLogCreateServiceRequest;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class MeetingLogService {

    private final MeetingLogRepository meetingLogRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public void create(MeetingLogCreateServiceRequest request) {
        Member member = memberRepository.findById(request.memberId());
        Team team = teamRepository.findById(request.teamId());

        meetingLogRepository.save(MeetingLog.create(request.content(), member, team, LocalDate.now()));
    }

}
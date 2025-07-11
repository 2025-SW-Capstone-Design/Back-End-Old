package soon.capstone.domain.team.service.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.service.dto.request.TeamCreateServiceRequest;
import soon.capstone.domain.team.service.dto.request.TeamInvitationServiceRequest;
import soon.capstone.domain.team.service.dto.request.TeamJoinServiceRequest;
import soon.capstone.domain.team.service.dto.response.TeamDetailResponse;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.IsNotTeamLeaderException;

import java.util.List;

import static soon.capstone.domain.teammember.entity.common.Role.isLeader;

@RequiredArgsConstructor
@Service
public class TeamService {

    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamCreationService teamCreationService;
    private final TeamInvitationService teamInvitationService;
    private final TeamJoinService teamJoinService;
    private final TeamReaderService teamReaderService;

    public Long createTeam(TeamCreateServiceRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId);

        return teamCreationService.createTeam(
            request.name(),
            request.organizationName(),
            request.description(),
            member
        );
    }

    public String generateInvitationCode(Long teamId, Long memberId) {
        validateTeamLeader(teamId, memberId);

        return teamInvitationService.generateInvitationCode(teamId);
    }

    public void sendInvitationEmails(TeamInvitationServiceRequest request, Long memberId) {
        validateTeamLeader(request.teamId(), memberId);

        teamInvitationService.sendInvitationEmails(request.teamId(), request.emails());
    }

    public Long joinTeamWithInvitationCode(TeamJoinServiceRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId);
        return teamJoinService.joinTeamWithInvitationCode(member, request.invitationCode());
    }

    public List<TeamDetailResponse> getTeamDetails(Long memberId) {
        return teamReaderService.getTeamDetails(memberId);
    }

    private void validateTeamLeader(Long teamId, Long memberId) {
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndMemberId(teamId, memberId);
        if (!isLeader(teamMember.getRole())) {
            throw new IsNotTeamLeaderException();
        }
    }

}
package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.milestone.service.dto.MilestoneCreationDto;
import soon.capstone.domain.milestone.service.dto.request.MilestoneCreateServiceRequest;
import soon.capstone.domain.milestone.service.port.MilestonePort;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.teammember.service.TeamMemberValidator;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;

@RequiredArgsConstructor
@Service
public class MilestoneService {

    private final MilestonePort milestonePort;
    private final TeamMemberValidator teamMemberValidator;
    private final MilestoneCreationService milestoneCreationService;

    public Long createMilestone(Long memberId, MilestoneCreateServiceRequest request) {
        Member member = milestonePort.getMember(memberId);
        Team team = milestonePort.getTeam(request.teamId());
        Project project = milestonePort.getProject(request.projectId());
        OAuthToken oAuthToken = milestonePort.getOAuthToken(memberId);

        teamMemberValidator.validateTeamMember(member, team);

        return milestoneCreationService.createMilestone(
                MilestoneCreationDto.of(
                        team.getOrganizationName(),
                        project.getTitle(),
                        oAuthToken.getToken(),
                        request.title(),
                        request.description(),
                        request.dueDate(),
                        request.startDate(),
                        member.getNickname(),
                        project
                )
        );
    }

}
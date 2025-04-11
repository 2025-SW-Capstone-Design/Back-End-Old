package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.milestone.service.dto.MilestoneUpdateDto;
import soon.capstone.domain.milestone.service.dto.request.MilestoneUpdateServiceRequest;
import soon.capstone.domain.milestone.service.dto.response.MilestoneDetailResponse;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.milestone.service.dto.MilestoneCreationDto;
import soon.capstone.domain.milestone.service.dto.request.MilestoneCreateServiceRequest;
import soon.capstone.domain.milestone.service.port.MilestonePort;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.teammember.service.TeamMemberValidator;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MilestoneService {

    private final MilestonePort milestonePort;
    private final TeamMemberValidator teamMemberValidator;
    private final MilestoneCreationService milestoneCreationService;
    private final MilestoneReadService milestoneReadService;
    private final MilestoneUpdateService milestoneUpdateService;

    public Long createMilestone(Long memberId, Long teamId, Long projectId, MilestoneCreateServiceRequest request) {
        Member member = milestonePort.getMember(memberId);
        Team team = milestonePort.getTeam(teamId);
        Project project = milestonePort.getProject(projectId);
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

    public List<MilestoneResponse> getMilestonesByProject(Long memberId, Long teamId, Long projectId) {
        Member member = milestonePort.getMember(memberId);
        Team team = milestonePort.getTeam(teamId);
        Project project = milestonePort.getProject(projectId);

        teamMemberValidator.validateTeamMember(member, team);

        return milestoneReadService.getMilestonesByProject(project);
    }

    public List<MilestoneResponse> getMilestonesByTeam(Long memberId, Long teamId) {
        Member member = milestonePort.getMember(memberId);
        Team team = milestonePort.getTeam(teamId);

        teamMemberValidator.validateTeamMember(member, team);

        return milestoneReadService.getMilestonesByTeam(team);
    }

    public MilestoneDetailResponse getMilestoneDetail(Long memberId, Long teamId, Long milestoneId) {
        Member member = milestonePort.getMember(memberId);
        Team team = milestonePort.getTeam(teamId);

        teamMemberValidator.validateTeamMember(member, team);

        return milestoneReadService.getMilestoneDetail(milestoneId);
    }

    public MilestoneResponse updateMilestone(Long memberId, Long teamId, Long projectId, Long milestoneId, MilestoneUpdateServiceRequest request) {
        Member member = milestonePort.getMember(memberId);
        Team team = milestonePort.getTeam(teamId);
        Project project = milestonePort.getProject(projectId);
        OAuthToken oAuthToken = milestonePort.getOAuthToken(memberId);

        teamMemberValidator.validateTeamMember(member, team);
        MilestoneUpdateDto milestoneUpdateDto = MilestoneUpdateDto.of(
                team.getOrganizationName(),
                project.getTitle(),
                oAuthToken.getToken(),
                request.title(),
                request.description(),
                request.dueDate(),
                request.startDate()
        );

        return milestoneUpdateService.updateMilestone(milestoneId, milestoneUpdateDto);
    }

}
package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.issue.service.dto.request.IssueLabelCreateServiceRequest;
import soon.capstone.domain.issue.service.dto.request.IssueLabelUpdateServiceRequest;
import soon.capstone.domain.issue.service.dto.request.IssueTemplateCreateServiceRequest;
import soon.capstone.domain.issue.service.dto.request.IssueTemplateUpdateServiceRequest;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

@RequiredArgsConstructor
@Service
public class IssueManagementService {

    private final IssueLabelService issueLabelService;
    private final IssueTemplateService issueTemplateService;

    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;

    public Long createIssueLabel(IssueLabelCreateServiceRequest request, Long memberId) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(memberId);
        Project project = projectRepository.findById(request.projectId());

        validateTeamMembership(member, team); // TODO: 차후 handler로 분리

        return issueLabelService.createIssueLabel(
            request.title(),
            request.description(),
            request.color(),
            project,
            member.getId(),
            team
        );
    }

    public void updateIssueLabel(IssueLabelUpdateServiceRequest request, Long memberId) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(memberId);
        Project project = projectRepository.findById(request.projectId());

        validateTeamMembership(member, team);

        issueLabelService.updateIssueLabel(
            request.labelId(),
            request.oldTitle(),
            request.newTitle(),
            request.description(),
            request.color(),
            request.organizationName(),
            request.repositoryName(),
            project,
            member.getId()
        );
    }

    public Long createIssueTemplate(IssueTemplateCreateServiceRequest request, Long memberId) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(memberId);
        Project project = projectRepository.findById(request.projectId());

        validateTeamMembership(member, team);

        return issueTemplateService.createIssueTemplate(
            request.title(),
            request.description(),
            request.content(),
            request.type(),
            project
        );
    }

    public void updateIssueTemplate(IssueTemplateUpdateServiceRequest request, Long memberId) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(memberId);
        Project project = projectRepository.findById(request.projectId());

        validateTeamMembership(member, team);

        issueTemplateService.updateIssueTemplate(
            request.issueTemplateId(),
            request.title(),
            request.description(),
            request.content(),
            request.type(),
            project
        );
    }

    private void validateTeamMembership(Member member, Team team) {
        if (!teamMemberRepository.existsByMemberAndTeam(member, team)) {
            throw new TeamNotAuthorizedException();
        }
    }

}
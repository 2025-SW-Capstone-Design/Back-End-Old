package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.issue.service.dto.request.*;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class IssueManagementService {

    private final IssueService issueService;
    private final IssueLabelService issueLabelService;
    private final IssueTemplateService issueTemplateService;

    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;

    public Long createIssue(IssueCreateServiceRequest request) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(request.memberId());
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndMemberId(team.getId(), member.getId());
        Project project = projectRepository.findById(request.projectId());
        Milestone milestone = milestoneRepository.findById(request.milestoneId());

        validateTeamMembership(member, team);

        return issueService.create(
            member.getId(),
            request.organizationName(),
            request.repositoryName(),
            request.title(),
            request.content(),
            request.assignees(),
            request.labels(),
            milestone,
            project,
            teamMember
        );
    }

    public void updateIssue(IssueUpdateServiceRequest request) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(request.memberId());
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndMemberId(team.getId(), member.getId());
        Milestone milestone = milestoneRepository.findById(request.milestoneId());

        validateTeamMembership(member, team);

        issueService.update(
            member.getId(),
            request.issueId(),
            request.organizationName(),
            request.repositoryName(),
            request.title(),
            request.content(),
            request.labels(),
            request.assignees(),
            request.state(),
            teamMember,
            milestone
        );
    }

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

    public void deleteIssueLabel(IssueLabelDeleteServiceRequest request, Long memberId) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(memberId);
        validateTeamMembership(member, team);

        issueLabelService.deleteIssueLabel(
            memberId,
            request.labelId(),
            request.organizationName(),
            request.repositoryName(),
            request.title()
        );
    }

    public List<IssueLabelDetailResponse> getIssueLabels(IssueLabelDetailServiceRequest request, Long memberId) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(memberId);
        Project project = projectRepository.findById(request.projectId());

        validateTeamMembership(member, team);

        return issueLabelService.getIssueLabels(
            memberId,
            team,
            project
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

    public IssueTemplateDetailResponse getIssueTemplate(Long teamId, Long issueTemplateId, Long memberId) {
        Team team = teamRepository.findById(teamId);
        Member member = memberRepository.findById(memberId);

        validateTeamMembership(member, team);

        return issueTemplateService.getIssueTemplate(issueTemplateId);
    }

    public List<IssueTemplateDetailResponse> getIssueTemplates(
        Long teamId,
        Long memberId,
        Long projectId,
        String type
    ) {
        Team team = teamRepository.findById(teamId);
        Member member = memberRepository.findById(memberId);
        Project project = projectRepository.findById(projectId);

        validateTeamMembership(member, team);

        return issueTemplateService.getIssueTemplates(type, project);
    }

    public void deleteIssueTemplate(Long teamId, Long issueTemplateId, Long memberId) {
        Team team = teamRepository.findById(teamId);
        Member member = memberRepository.findById(memberId);

        validateTeamMembership(member, team);

        issueTemplateService.deleteIssueTemplate(issueTemplateId);
    }

    private void validateTeamMembership(Member member, Team team) {
        if (!teamMemberRepository.existsByMemberAndTeam(member, team)) {
            throw new TeamNotAuthorizedException();
        }
    }

}
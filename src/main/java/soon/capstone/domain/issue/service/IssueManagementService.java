package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.issue.service.dto.request.IssueLabelCreateServiceRequest;
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

    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;

    public Long createIssueLabel(IssueLabelCreateServiceRequest request, Long memberId) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(memberId);
        Project project = projectRepository.findById(request.projectId());

        if (!teamMemberRepository.existsByMemberAndTeam(member, team)) {
            throw new TeamNotAuthorizedException();
        }

        return issueLabelService.createIssueLabel(
            request.title(),
            request.description(),
            request.color(),
            project,
            member.getId(),
            team
        );
    }

}
package soon.capstone.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import soon.capstone.domain.issue.service.IssueLabelService;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.project.service.dto.request.TeamCreatedEvent;
import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.domain.project.service.dto.response.RepositoryCreationEvent;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;

    private final RepositoryCreationService repositoryCreationService;
    private final OrganizationProjectCreationService organizationProjectCreationService;
    private final ProjectReadService projectReadService;
    private final IssueLabelService issueLabelService;

    public List<ProjectDetailResponse> getProjects(Long memberId, Long teamId) {
        Member member = memberRepository.findById(memberId);
        return projectReadService.getProjects(member, teamId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createRepository(TeamCreatedEvent event) {
        Member member = memberRepository.findById(event.memberId());
        repositoryCreationService.createRepository(event.teamId(), member.getNickname(), event.oauthToken());

        Team team = teamRepository.findById(event.teamId());
        List<Project> projects = projectRepository.findAllByTeamIdAndCreator(team.getId(), member.getNickname());
        projects.forEach(project -> {
            issueLabelService.initializeIssueLabels(member.getId(), project, team);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createProject(RepositoryCreationEvent repositoryCreationEvent) {
        organizationProjectCreationService.createProject(
            repositoryCreationEvent.organizationName(),
            repositoryCreationEvent.oauthToken(),
            repositoryCreationEvent.repositoryId(),
            repositoryCreationEvent.repoName()

        );
    }

}
package soon.capstone.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.service.dto.response.RepositoryCreationEvent;
import soon.capstone.domain.project.service.dto.request.TeamCreatedEvent;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final MemberRepository memberRepository;
    private final RepositoryCreationService repositoryCreationService;
    private final OrganizationProjectCreationService organizationProjectCreationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createRepository(TeamCreatedEvent event) {
        Member member = memberRepository.findById(event.memberId());
        repositoryCreationService.createRepository(event.teamId(), member.getNickname(), event.oauthToken());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createProject(RepositoryCreationEvent repositoryCreationEvent) {
        organizationProjectCreationService.createProject(repositoryCreationEvent.organizationName(), repositoryCreationEvent.oauthToken());
    }

}
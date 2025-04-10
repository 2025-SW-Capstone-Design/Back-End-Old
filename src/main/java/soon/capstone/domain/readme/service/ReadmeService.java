package soon.capstone.domain.readme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.readme.entity.Readme;
import soon.capstone.domain.readme.repository.ReadmeRepository;
import soon.capstone.domain.readme.service.dto.request.ReadmeCreateServiceRequest;
import soon.capstone.domain.readme.service.dto.request.ReadmeUpdateServiceRequest;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReadmeService {

    private final ReadmeRepository readmeRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public Long create(ReadmeCreateServiceRequest request) {
        return saveNewReadme(
            request.teamId(),
            request.memberId(),
            request.projectId(),
            request.title(),
            request.content()
        );
    }

    @Transactional
    public Long update(ReadmeUpdateServiceRequest request) {
        return saveNewReadme(
            request.teamId(),
            request.memberId(),
            request.projectId(),
            request.title(),
            request.content()
        );
    }

    private Long saveNewReadme(Long teamId, Long memberId, Long projectId, String title, String content) {
        Team team = teamRepository.findById(teamId);
        Member member = memberRepository.findById(memberId);
        Project project = projectRepository.findById(projectId);

        validateTeamMembership(member, team);

        Optional<Readme> latestReadme = readmeRepository.findByProjectIdAndLatestIsTrue(projectId);

        int newVersion = 1;
        if (latestReadme.isPresent()) {
            Readme current = latestReadme.get();
            current.markAsOld();
            newVersion = current.getVersion() + 1;
        }

        Readme readme = Readme.createNew(title, content, newVersion, member, project);
        return readmeRepository.save(readme);
    }

    private void validateTeamMembership(Member member, Team team) {
        if (!teamMemberRepository.existsByMemberAndTeam(member, team)) {
            throw new TeamNotAuthorizedException();
        }
    }

}

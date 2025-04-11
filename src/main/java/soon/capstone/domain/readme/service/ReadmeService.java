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
import soon.capstone.domain.readme.service.dto.request.*;
import soon.capstone.domain.readme.service.dto.response.ReadmeDetailResponse;
import soon.capstone.domain.readme.service.dto.response.ReadmeListResponse;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional
    public void delete(ReadmeDeleteServiceRequest request) {
        validateTeamMembership(
            memberRepository.findById(request.memberId()),
            teamRepository.findById(request.teamId())
        );

        Readme readme = readmeRepository.findById(request.readmeId());
        readmeRepository.delete(readme);
    }

    @Transactional(readOnly = true)
    public List<ReadmeListResponse> getReadmes(ReadmesListServiceRequest request) {
        validateTeamMembership(
            memberRepository.findById(request.memberId()),
            teamRepository.findById(request.teamId())
        );

        return readmeRepository.findAllByProjectIdOrderByVersionDesc(request.projectId())
            .stream()
            .map(ReadmeListResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReadmeDetailResponse getDetail(ReadmeDetailServiceRequest request) {
        validateTeamMembership(
            memberRepository.findById(request.memberId()),
            teamRepository.findById(request.teamId())
        );

        return ReadmeDetailResponse.from(readmeRepository.findById(request.readmeId()));
    }

    @Transactional
    public Long rollback(ReadmeRollbackServiceRequest request) {
        Member member = memberRepository.findById(request.memberId());
        Team team = teamRepository.findById(request.teamId());
        validateTeamMembership(member, team);

        Readme source = readmeRepository.findById(request.readmeId());
        int newVersion = updateLatestReadme(request.projectId());

        Readme rollbacked = Readme.rollbackFrom(source, newVersion, member);
        return readmeRepository.save(rollbacked);
    }

    private Long saveNewReadme(Long teamId, Long memberId, Long projectId, String title, String content) {
        Member member = memberRepository.findById(memberId);
        Team team = teamRepository.findById(teamId);
        Project project = projectRepository.findById(projectId);
        validateTeamMembership(member, team);

        int newVersion = updateLatestReadme(projectId);

        Readme readme = Readme.createNew(title, content, newVersion, member, project);
        return readmeRepository.save(readme);
    }

    private int updateLatestReadme(Long projectId) {
        Optional<Readme> latestReadme = readmeRepository.findByProjectIdAndLatestIsTrue(projectId);

        if (latestReadme.isEmpty()) {
            return 1;
        }

        Readme current = latestReadme.get();
        current.markAsOld();
        return current.getVersion() + 1;
    }

    private void validateTeamMembership(Member member, Team team) {
        if (!teamMemberRepository.existsByMemberAndTeam(member, team)) {
            throw new TeamNotAuthorizedException();
        }
    }

}
package soon.capstone.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.teammember.TeamMemberNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectReadService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<ProjectDetailResponse> getProjects(Member member, Long teamId) {
        Team team = teamRepository.findById(teamId);
        if (!teamMemberRepository.existsByMemberAndTeam(member, team)) {
            throw new TeamMemberNotFoundException();
        }

        return projectRepository.getProjects(team);
    }
}

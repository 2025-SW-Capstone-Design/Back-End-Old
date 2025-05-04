package soon.capstone.domain.team.service.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.team.service.dto.response.TeamDetailResponse;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamReaderService {

    private final TeamMemberRepository teamMemberRepository;

    @Transactional(readOnly = true)
    public List<TeamDetailResponse> getTeamDetails(Long memberId) {
        List<TeamMember> teamMembers = teamMemberRepository.findAllByMemberId(memberId);
        return teamMembers.stream()
            .map(teamMember -> TeamDetailResponse.from(teamMember.getTeam()))
            .toList();
    }

}
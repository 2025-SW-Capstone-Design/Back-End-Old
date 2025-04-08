package soon.capstone.domain.milestone.service.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.milestone.service.port.MilestonePort;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

@RequiredArgsConstructor
@Component
public class MilestoneAdapter implements MilestonePort {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final OAuthTokenRepository oAuthTokenRepository;

    @Override
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    public Team getTeam(Long teamId) {
        return teamRepository.findById(teamId);
    }

    @Override
    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public OAuthToken getOAuthToken(Long memberId) {
        return oAuthTokenRepository.findByMemberId(memberId);
    }
}

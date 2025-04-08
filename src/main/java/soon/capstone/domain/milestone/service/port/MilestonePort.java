package soon.capstone.domain.milestone.service.port;

import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;

public interface MilestonePort {
    Member getMember(Long memberId);
    Team getTeam(Long teamId);
    Project getProject(Long projectId);
    OAuthToken getOAuthToken(Long memberId);
}

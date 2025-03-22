package soon.capstone.infrastructure.github.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import soon.capstone.global.exception.github.OAuthTokenExpiredException;
import soon.capstone.infrastructure.github.dto.GithubOrganizationMembershipDto;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubOrganizationService {

    private static final String MEMBERSHIP_URL = "/user/memberships/orgs/{organizationName}";
    private static final String ORGANIZATION_MEMBER_URL = "/orgs/{organizationName}/memberships/{username}";

    private final RestClientConfig restClientConfig;
    private final OAuthTokenRepository oAuthTokenRepository;

    public boolean isAdminInOrganization(String oauthToken, String organizationName) {
        // TODO: 차후 리펙토링 예정
        RestClient restClient = restClientConfig.githubRestClient(oauthToken);

        try {
            GithubOrganizationMembershipDto membershipDto = restClient.get()
                .uri(MEMBERSHIP_URL.replace("{organizationName}", organizationName))
                .retrieve()
                .body(GithubOrganizationMembershipDto.class);

            return membershipDto != null && membershipDto.isAdminWithActiveStatus();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }


    public void addMemberToOrganization(Long leaderId, String memberNickname, String organizationName) {
        try {
            String leaderOAuthToken = oAuthTokenRepository.findByMemberId(leaderId)
                .getToken();
            String uri = ORGANIZATION_MEMBER_URL
                .replace("{organizationName}", organizationName)
                .replace("{username}", memberNickname);

            restClientConfig.githubRestClient(leaderOAuthToken)
                .put()
                .uri(uri)
                .retrieve()
                .body(Void.class);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == UNAUTHORIZED.value()) {
                throw new OAuthTokenExpiredException();
            }

            log.error("organization 멤버 추가 중 에러 발생 member: {}, organization: {}", memberNickname, organizationName);
        }
    }

}
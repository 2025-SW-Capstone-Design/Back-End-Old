package soon.capstone.external.github.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import soon.capstone.external.config.RestClientConfig;
import soon.capstone.external.github.dto.GithubOrganizationMembershipDto;

@RequiredArgsConstructor
@Service
public class GithubOrganizationService {

    private static final String MEMBERSHIP_URL = "/user/memberships/orgs/{organizationName}";
    private final RestClientConfig restClientConfig;

    public boolean isAdminInOrganization(String oauthToken, String organizationName) {
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

}
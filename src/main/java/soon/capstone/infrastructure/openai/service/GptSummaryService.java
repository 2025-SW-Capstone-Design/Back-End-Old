package soon.capstone.infrastructure.openai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import soon.capstone.infrastructure.openai.service.dto.request.GptSummaryServiceRequest;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

@Service
public class GptSummaryService {

    private final RestClient restClient;
    private final String model;

    public GptSummaryService(
        RestClientConfig restClientConfig,
        @Value("${openai.model}") String model
    ) {
        this.restClient = restClientConfig.openAiRestClient();
        this.model = model;
    }

    public String summaryToText(String text, boolean isFinal) {
        return restClient.post()
            .body(GptSummaryServiceRequest.of(text, model, isFinal))
            .retrieve()
            .body(String.class);
    }

}
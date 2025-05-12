package soon.capstone.infrastructure.openai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import soon.capstone.infrastructure.openai.service.dto.OpenAiResponse;
import soon.capstone.infrastructure.openai.service.dto.request.GptSummaryServiceRequest;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

import java.util.Objects;

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

    // TODO: 차후 비동기 처리로 변경
    public String summaryToText(String text, boolean isFinal) {
        OpenAiResponse response = restClient.post()
            .body(GptSummaryServiceRequest.of(text, model, isFinal))
            .retrieve()
            .body(OpenAiResponse.class);

        return Objects.requireNonNull(response).choices().getFirst().message().content();
    }

}
package soon.capstone.infrastructure.openai.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import soon.capstone.infrastructure.openai.service.dto.OpenAiResponse;
import soon.capstone.infrastructure.openai.service.dto.request.GptSummaryServiceRequest;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

import java.util.Objects;

@Service
public class GptSummaryService {

    private final RestClient restClient;
    private final String model;
    private final String sttModel;

    public GptSummaryService(
        RestClientConfig restClientConfig,
        @Value("${openai.model}") String model,
        @Value("${openai.stt-model}") String sttModel
    ) {
        this.restClient = restClientConfig.openAiRestClient();
        this.model = model;
        this.sttModel = sttModel;
    }

    // TODO: 차후 비동기 처리로 변경
    public String summaryToText(String text, boolean isFinal) {
        OpenAiResponse response = restClient.post()
            .body(GptSummaryServiceRequest.of(text, model, isFinal))
            .retrieve()
            .body(OpenAiResponse.class);

        return Objects.requireNonNull(response).choices().getFirst().message().content();
    }

    public String summaryToText(byte[] audioBytes, String filename) {
        String text = sttText(audioBytes, filename);
        System.out.println(text);

        OpenAiResponse response = restClient.post()
            .uri("https://api.openai.com/v1/chat/completions")
            .body(GptSummaryServiceRequest.of(text, model, true))
            .retrieve()
            .body(OpenAiResponse.class);

        return Objects.requireNonNull(response).choices().getFirst().message().content();
    }

    private String sttText(byte[] audioBytes, String filename) {
        ByteArrayResource resource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        multipartBody.add("file", resource);
        multipartBody.add("model", sttModel);
        multipartBody.add("language", "ko");

        return Objects.requireNonNull(restClient.post()

                .uri("/audio/transcriptions")
                .header("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(multipartBody)
                .retrieve()
                .body(WhisperResponse.class))
            .getText();  // 필요시 JSON 파싱해서 텍스트만 추출
    }


    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class WhisperResponse {
        private String text;
    }

}
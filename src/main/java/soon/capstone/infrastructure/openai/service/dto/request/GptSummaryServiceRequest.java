package soon.capstone.infrastructure.openai.service.dto.request;

import lombok.Builder;
import soon.capstone.infrastructure.openai.common.PromptType;

import java.util.List;

import static soon.capstone.infrastructure.openai.common.PromptType.FINAL_SUMMARY;

@Builder
public record GptSummaryServiceRequest(

    String model,
    List<Message> messages

) {

    @Builder
    public record Message(String role, String content) {
    }

    public static GptSummaryServiceRequest of(String text, String model, boolean isFinal) {
//        PromptType promptType = (isFinal) ? PromptType.FINAL_SUMMARY : PromptType.INTERMEDIATE_SUMMARY;

        return GptSummaryServiceRequest.builder()
            .model(model)
            .messages(List.of(
                Message.builder()
                    .role("system")
                    .content(FINAL_SUMMARY.getPrompt())
                    .build(),
                Message.builder()
                    .role("user")
                    .content(text)
                    .build()
            ))
            .build();
    }

}
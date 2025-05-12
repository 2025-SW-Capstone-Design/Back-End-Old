package soon.capstone.infrastructure.openai.service.dto;

import java.util.List;

public record OpenAiResponse(

    List<Choice> choices

) {

    public record Choice(Message message) {
    }

    public record Message(String content) {
    }

}
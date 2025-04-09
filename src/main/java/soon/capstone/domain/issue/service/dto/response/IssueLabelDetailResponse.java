package soon.capstone.domain.issue.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class IssueLabelDetailResponse {
    private Long id;
    private String name;
    private String color;
    private String description;

    @Builder(toBuilder = true)
    private IssueLabelDetailResponse(Long id, String name, String color, String description) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
    }

    public IssueLabelDetailResponse withId(Long newId) {
        return toBuilder().id(newId).build();
    }

}
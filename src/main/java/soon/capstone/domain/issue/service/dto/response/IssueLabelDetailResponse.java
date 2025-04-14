package soon.capstone.domain.issue.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class IssueLabelDetailResponse {
    private Long labelId;
    private String name;
    private String color;
    private String description;

    @Builder(toBuilder = true)
    private IssueLabelDetailResponse(Long labelId, String name, String color, String description) {
        this.labelId = labelId;
        this.name = name;
        this.color = color;
        this.description = description;
    }

    public IssueLabelDetailResponse withLabelId(Long newId) {
        return toBuilder().labelId(newId).build();
    }

}
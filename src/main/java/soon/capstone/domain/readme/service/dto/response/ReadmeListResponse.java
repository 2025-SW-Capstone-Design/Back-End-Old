package soon.capstone.domain.readme.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.readme.entity.Readme;

@Builder
public record ReadmeListResponse(

    Long readmeId,
    String title,
    int version,
    String writer,
    boolean isLatest

) {

    public static ReadmeListResponse from(Readme readme) {
        return ReadmeListResponse.builder()
            .readmeId(readme.getId())
            .title(readme.getTitle())
            .version(readme.getVersion())
            .writer(readme.getMember().getNickname())
            .isLatest(readme.isLatest())
            .build();
    }

}
package soon.capstone.domain.readme.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.readme.entity.Readme;

@Builder
public record ReadmeDetailResponse(

    Long readmeId,
    String title,
    String content,
    int version,
    boolean isLatest,
    String writer,
    String projectName

) {

    public static ReadmeDetailResponse from(Readme readme) {
        return ReadmeDetailResponse.builder()
            .readmeId(readme.getId())
            .title(readme.getTitle())
            .content(readme.getContent())
            .version(readme.getVersion())
            .isLatest(readme.isLatest())
            .writer(readme.getMember().getNickname())
            .projectName(readme.getProject().getTitle())
            .build();
    }

}
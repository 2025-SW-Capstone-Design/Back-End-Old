package soon.capstone.global.exception.project;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class ProjectNotFoundException extends RootException {
    public ProjectNotFoundException() {
        super(ErrorDetail.PROJECT_NOT_FOUND);
    }

    public ProjectNotFoundException(Throwable cause) {
        super(ErrorDetail.PROJECT_NOT_FOUND, cause);
    }
}

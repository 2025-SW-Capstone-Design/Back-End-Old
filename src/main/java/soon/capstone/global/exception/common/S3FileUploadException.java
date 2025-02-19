package soon.capstone.global.exception.common;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class S3FileUploadException extends RootException {

    public S3FileUploadException() {
        super(ErrorDetail.S3_FILE_UPLOAD);
    }

    public S3FileUploadException(Throwable cause) {
        super(ErrorDetail.S3_FILE_UPLOAD, cause);
    }

}
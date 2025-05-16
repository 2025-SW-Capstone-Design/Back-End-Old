package soon.capstone.global.exception.meetinglog;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class MeetingLogNotFoundException extends RootException {

    public MeetingLogNotFoundException() {
        super(ErrorDetail.MEETING_LOG_NOT_FOUND);
    }

    public MeetingLogNotFoundException(Throwable cause) {
        super(ErrorDetail.MEETING_LOG_NOT_FOUND, cause);
    }

}
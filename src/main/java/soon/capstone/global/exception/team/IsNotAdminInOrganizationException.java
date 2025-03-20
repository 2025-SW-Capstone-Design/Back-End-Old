package soon.capstone.global.exception.team;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class IsNotAdminInOrganizationException extends RootException {

    public IsNotAdminInOrganizationException() {
        super(ErrorDetail.IS_NOT_ADMIN_IN_ORGANIZATION);
    }

    public IsNotAdminInOrganizationException(Throwable cause) {
        super(ErrorDetail.IS_NOT_ADMIN_IN_ORGANIZATION, cause);
    }

}

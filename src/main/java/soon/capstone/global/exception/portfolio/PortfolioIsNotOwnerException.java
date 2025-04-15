package soon.capstone.global.exception.portfolio;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class PortfolioIsNotOwnerException extends RootException {
    public PortfolioIsNotOwnerException() {
        super(ErrorDetail.PORTFOLIO_IS_NOT_OWNER);
    }

    public PortfolioIsNotOwnerException(Throwable cause) {
        super(ErrorDetail.PORTFOLIO_IS_NOT_OWNER, cause);
    }
}

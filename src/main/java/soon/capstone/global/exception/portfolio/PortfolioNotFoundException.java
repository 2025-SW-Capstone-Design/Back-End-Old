package soon.capstone.global.exception.portfolio;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class PortfolioNotFoundException extends RootException {
    public PortfolioNotFoundException() {
        super(ErrorDetail.PORTFOLIO_NOT_FOUND);
    }

    public PortfolioNotFoundException(Throwable cause) {
        super(ErrorDetail.PORTFOLIO_NOT_FOUND, cause);
    }
}

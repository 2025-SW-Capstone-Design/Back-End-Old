package soon.capstone.global.exception.portfolio;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class PortfolioDuplicateTitleException extends RootException {
    public PortfolioDuplicateTitleException() {
        super(ErrorDetail.PORTFOLIO_DUPLICATE_TITLE);
    }

    public PortfolioDuplicateTitleException(Throwable cause) {
        super(ErrorDetail.PORTFOLIO_DUPLICATE_TITLE, cause);
    }
}

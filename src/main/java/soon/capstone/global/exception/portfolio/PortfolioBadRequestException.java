package soon.capstone.global.exception.portfolio;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class PortfolioBadRequestException extends RootException {
  public PortfolioBadRequestException() {
    super(ErrorDetail.PORTFOLIO_BAD_REQUEST);
  }

  public PortfolioBadRequestException(Throwable cause) {
    super(ErrorDetail.PORTFOLIO_BAD_REQUEST, cause);
  }
}

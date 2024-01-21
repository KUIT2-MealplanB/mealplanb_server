package mealplanb.server.common.exception.jwt.bad_request;

import mealplanb.server.common.response.status.ResponseStatus;
import lombok.Getter;


@Getter
public class JwtUnsupportedTokenException extends JwtBadRequestException {

    private final ResponseStatus exceptionStatus;

    public JwtUnsupportedTokenException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
        this.exceptionStatus = exceptionStatus;
    }
}

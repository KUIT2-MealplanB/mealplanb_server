package mealplanb.server.common.exception.jwt.unauthorized;

import lombok.Getter;
import mealplanb.server.common.response.status.ResponseStatus;

@Getter
public class JwtInvalidTokenException extends JwtUnauthorizedTokenException {

    private final ResponseStatus exceptionStatus;

    public JwtInvalidTokenException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
        this.exceptionStatus = exceptionStatus;
    }
}

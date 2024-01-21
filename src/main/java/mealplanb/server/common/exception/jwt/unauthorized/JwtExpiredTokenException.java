package mealplanb.server.common.exception.jwt.unauthorized;

import lombok.Getter;
import mealplanb.server.common.response.status.ResponseStatus;

@Getter
public class JwtExpiredTokenException extends JwtUnauthorizedTokenException {

    private final ResponseStatus exceptionStatus;

    public JwtExpiredTokenException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
        this.exceptionStatus = exceptionStatus;
    }
}

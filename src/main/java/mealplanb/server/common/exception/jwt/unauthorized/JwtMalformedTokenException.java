package mealplanb.server.common.exception.jwt.unauthorized;

import lombok.Getter;
import mealplanb.server.common.response.status.ResponseStatus;

@Getter
public class JwtMalformedTokenException extends JwtUnauthorizedTokenException {

    private final ResponseStatus exceptionStatus;

    public JwtMalformedTokenException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
        this.exceptionStatus = exceptionStatus;
    }
}
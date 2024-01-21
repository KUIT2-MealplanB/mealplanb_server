package mealplanb.server.common.exception.jwt.bad_request;

import lombok.Getter;
import mealplanb.server.common.response.status.ResponseStatus;

@Getter
public class JwtBadRequestException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public JwtBadRequestException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}

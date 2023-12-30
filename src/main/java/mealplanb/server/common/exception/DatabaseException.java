package mealplanb.server.common.exception;

import lombok.Getter;
import mealplanb.server.common.response.status.ResponseStatus;

@Getter
public class DatabaseException extends RuntimeException{

    private final ResponseStatus exceptionStatus;

    public DatabaseException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}

package mealplanb.server.common.exception;

import lombok.Getter;
import mealplanb.server.common.response.status.ResponseStatus;

@Getter
public class MealException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public MealException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public MealException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}

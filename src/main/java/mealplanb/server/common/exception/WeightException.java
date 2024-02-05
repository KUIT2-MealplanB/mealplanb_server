package mealplanb.server.common.exception;

import lombok.Getter;
import mealplanb.server.common.response.status.ResponseStatus;

@Getter
public class WeightException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public WeightException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public WeightException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}
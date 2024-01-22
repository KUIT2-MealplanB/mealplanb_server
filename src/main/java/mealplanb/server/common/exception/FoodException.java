package mealplanb.server.common.exception;

import mealplanb.server.common.response.status.ResponseStatus;

public class FoodException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public FoodException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public FoodException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}
package mealplanb.server.common.exception;

import lombok.Getter;
import mealplanb.server.common.response.status.ResponseStatus;

@Getter
public class ChatException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public ChatException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public ChatException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}
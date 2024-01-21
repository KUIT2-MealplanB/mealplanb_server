package mealplanb.server.common.exception;

import mealplanb.server.common.response.status.BaseExceptionResponseStatus;

public class MemberException extends RuntimeException{
    private final BaseExceptionResponseStatus status;

    public MemberException(BaseExceptionResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public BaseExceptionResponseStatus getStatus() {
        return status;
    }
}

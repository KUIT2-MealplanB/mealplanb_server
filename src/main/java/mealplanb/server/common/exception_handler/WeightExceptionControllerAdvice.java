package mealplanb.server.common.exception_handler;

import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.WeightException;
import mealplanb.server.common.response.BaseErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class WeightExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WeightException.class)
    public BaseErrorResponse handle_WeightException(WeightException e) {
        log.error("[handle_WeightException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}
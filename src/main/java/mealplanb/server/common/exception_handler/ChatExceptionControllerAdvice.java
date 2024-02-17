package mealplanb.server.common.exception_handler;

import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import mealplanb.server.common.exception.ChatException;
import mealplanb.server.common.response.BaseErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class ChatExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatException.class)
    public BaseErrorResponse handle_ChatException(ChatException e) {
        log.error("[handle_ChatException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}

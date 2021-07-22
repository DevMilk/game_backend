package com.devmilk.gameserver.auth.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class, GroupNotFoundException.class, TournamentNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String  resourceNotFoundException(UserNotFoundException exc, WebRequest request) {
        return exc.getMessage();

    }

    @ExceptionHandler(value = {ConditionsDoesntMetException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public String resourceNotFoundException(ConditionsDoesntMetException exc, WebRequest request) {
        return exc.getMessage();
    }
}
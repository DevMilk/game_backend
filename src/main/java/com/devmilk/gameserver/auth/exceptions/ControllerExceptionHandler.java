package com.devmilk.gameserver.auth.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class, RecordNotFoundException.class,GroupNotFoundException.class, TournamentNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String  resourceNotFoundException(RuntimeException exc, WebRequest request) {
        return exc.getMessage();

    }

    @ExceptionHandler(value = {ConditionsDoesntMetException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public String notAuthorized(RuntimeException exc, WebRequest request) {
        return exc.getMessage();
    }

    @ExceptionHandler(value = {UserAlreadyInGroupException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public String conflict(RuntimeException exc, WebRequest request) {
        return exc.getMessage();
    }
}
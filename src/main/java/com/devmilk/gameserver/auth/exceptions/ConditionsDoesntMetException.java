package com.devmilk.gameserver.auth.exceptions;

public class ConditionsDoesntMetException extends RuntimeException{
    public ConditionsDoesntMetException(String message) {
        super(message);
    }
}

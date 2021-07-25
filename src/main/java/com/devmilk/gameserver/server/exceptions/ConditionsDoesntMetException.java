package com.devmilk.gameserver.server.exceptions;

public class ConditionsDoesntMetException extends RuntimeException{
    public ConditionsDoesntMetException(String message) {
        super(message);
    }
}

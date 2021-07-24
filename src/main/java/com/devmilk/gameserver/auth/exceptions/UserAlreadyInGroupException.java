package com.devmilk.gameserver.auth.exceptions;

public class UserAlreadyInGroupException extends RuntimeException {
    public UserAlreadyInGroupException(String message) {super(message);    }
}

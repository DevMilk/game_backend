package com.devmilk.gameserver.auth.exceptions;

public class UserNotFoundException extends Throwable {
    public UserNotFoundException(String user_not_found) {
        super(user_not_found);
    }
}

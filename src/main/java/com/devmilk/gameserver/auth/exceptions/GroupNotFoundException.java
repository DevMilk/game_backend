package com.devmilk.gameserver.auth.exceptions;

public class GroupNotFoundException extends RuntimeException{
    public GroupNotFoundException(String message) {
        super(message);
    }

}

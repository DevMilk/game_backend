package com.devmilk.gameserver.auth.exceptions;

public class TournamentNotFoundException extends Throwable{
    public TournamentNotFoundException(String message) {
        super(message);
    }
}

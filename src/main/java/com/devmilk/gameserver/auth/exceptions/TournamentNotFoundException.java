package com.devmilk.gameserver.auth.exceptions;

public class TournamentNotFoundException extends RuntimeException{
    public TournamentNotFoundException(String message) {
        super(message);
    }
}

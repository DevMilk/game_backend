package com.devmilk.gameserver.server.exceptions;

public class TournamentNotFoundException extends RuntimeException{
    public TournamentNotFoundException(String message) {
        super(message);
    }
}

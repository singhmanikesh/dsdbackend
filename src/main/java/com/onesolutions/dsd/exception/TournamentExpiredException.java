package com.onesolutions.dsd.exception;

public class TournamentExpiredException extends TournamentException {
    public TournamentExpiredException(String message) {
        super("TOURNAMENT_EXPIRED", message);
    }
}


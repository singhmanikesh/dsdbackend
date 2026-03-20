package com.onesolutions.dsd.exception;

public class UserAlreadyJoinedException extends TournamentException {
    public UserAlreadyJoinedException(String message) {
        super("USER_ALREADY_JOINED", message);
    }
}


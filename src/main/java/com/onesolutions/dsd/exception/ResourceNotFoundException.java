package com.onesolutions.dsd.exception;

public class ResourceNotFoundException extends TournamentException {
    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}


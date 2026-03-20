package com.onesolutions.dsd.exception;

public class TournamentException extends RuntimeException {
    private String errorCode;
    
    public TournamentException(String message) {
        super(message);
        this.errorCode = "TOURNAMENT_ERROR";
    }
    
    public TournamentException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}


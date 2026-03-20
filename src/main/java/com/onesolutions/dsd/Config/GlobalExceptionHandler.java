package com.onesolutions.dsd.Config;

import com.onesolutions.dsd.dto.ErrorResponseDTO;
import com.onesolutions.dsd.exception.ResourceNotFoundException;
import com.onesolutions.dsd.exception.TournamentException;
import com.onesolutions.dsd.exception.TournamentExpiredException;
import com.onesolutions.dsd.exception.UserAlreadyJoinedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyJoinedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyJoinedException(
            UserAlreadyJoinedException ex,
            HttpServletRequest request) {
        
        log.warn("User already joined tournament: {}", ex.getMessage());
        
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .error("CONFLICT")
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .timestamp(System.currentTimeMillis())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(TournamentExpiredException.class)
    public ResponseEntity<ErrorResponseDTO> handleTournamentExpiredException(
            TournamentExpiredException ex,
            HttpServletRequest request) {
        
        log.warn("Tournament expired: {}", ex.getMessage());
        
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .error("BAD_REQUEST")
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .timestamp(System.currentTimeMillis())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .error("NOT_FOUND")
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .timestamp(System.currentTimeMillis())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TournamentException.class)
    public ResponseEntity<ErrorResponseDTO> handleTournamentException(
            TournamentException ex,
            HttpServletRequest request) {
        
        log.error("Tournament exception: {}", ex.getMessage());
        
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .error("BAD_REQUEST")
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .timestamp(System.currentTimeMillis())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        
        log.error("Unexpected runtime exception", ex);
        
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .error("INTERNAL_SERVER_ERROR")
                .errorCode("SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .timestamp(System.currentTimeMillis())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected exception", ex);
        
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .error("INTERNAL_SERVER_ERROR")
                .errorCode("GENERIC_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .timestamp(System.currentTimeMillis())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


package com.example.spotifyscrobble.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse>userAlreadyExists(UserAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                ex.getMessage(),
                Instant.now())
        );
    }

    @ExceptionHandler(ArtistNotFoundException.class)
    public ResponseEntity<ErrorResponse> artistNotFound(ArtistNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                ex.getMessage(),
                Instant.now()
        ));
    }

    @ExceptionHandler(ArtistAlreadyExists.class)
    public ResponseEntity<ErrorResponse> artistAlreadyExists(ArtistAlreadyExists ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                ex.getMessage(),
                Instant.now()
        ));
    }

    @ExceptionHandler(TrackAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> trackAlreadyExists(TrackAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                ex.getMessage(),
                Instant.now()
        ));
    }

    @ExceptionHandler(ArtistAlreadyExistsInStatsException.class)
    public ResponseEntity<ErrorResponse> artistAlreadyExistsInStatistics(ArtistAlreadyExistsInStatsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                ex.getMessage(),
                Instant.now()
        ));
    }

    @ExceptionHandler(TrackStatAlreadyExists.class)
    public ResponseEntity<ErrorResponse> trackAlreadyExistsInStatistics(TrackAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                ex.getMessage(),
                Instant.now()
        ));
    }

    @ExceptionHandler(TrackNotFoundException.class)
    public ResponseEntity<ErrorResponse> trackNotFound(TrackNotFoundException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                ex.getMessage(),
                Instant.now()
        ));
    }

}

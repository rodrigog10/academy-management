package com.academia.gestao.exceptions;

import com.academia.gestao.responses.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<StandardError> handleResourceNotFoundTreatment(ResourceNotFoundException e, HttpServletRequest request) {

    HttpStatus status = HttpStatus.NOT_FOUND;
    StandardError structuredErrorMessage = new StandardError(
            Instant.now(),
            status.value(),
            "Resource not found.",
            e.getMessage(),
            request.getRequestURI()
    );
        return ResponseEntity.status(status).body(structuredErrorMessage);
}

@ExceptionHandler({BusinessRuleException.class})
    public ResponseEntity<StandardError> handleBusinessRuleException(BusinessRuleException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    StandardError structuredErrorMessage = new StandardError(
            Instant.now(),
            status.value(),
            "Business rule violation.",
            e.getMessage(),
            request.getRequestURI()
    );
            return ResponseEntity.status(status).body(structuredErrorMessage);
}

@ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<StandardError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {

    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(" | "));

    StandardError structuredErrorMessage = new StandardError(
            Instant.now(),
            status.value(),
            "Invalid fields.",
            errorMessage,
            request.getRequestURI()
    );
            return ResponseEntity.status(status).body(structuredErrorMessage);
}

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolation(
            DataIntegrityViolationException e,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        String errorDetail = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();

        StandardError structuredErrorMessage = new StandardError(
                Instant.now(),
                status.value(),
                "Database integrity violation.",
                errorDetail,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(structuredErrorMessage);
    }

}

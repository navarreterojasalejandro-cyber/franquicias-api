package com.ejemplo.franquicias.api;

import com.ejemplo.franquicias.service.ResourceNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(ResourceNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validation(WebExchangeBindException exception) {
        String message = exception.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Map.of("error", message.isBlank() ? "Solicitud inválida" : message);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict() {
        return Map.of("error", "El recurso cambió por otra operación. Vuelve a consultar e intenta de nuevo.");
    }
}

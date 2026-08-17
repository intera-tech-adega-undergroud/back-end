package com.intera.adegaunderground.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        String errorId = UUID.randomUUID().toString();
        logger.warn("Erro de validação [{}]: {}", errorId, ex.getMessage());

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now().toString(),
                "errorId", errorId,
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception ex) {
        String errorId = UUID.randomUUID().toString();
        logger.error("Erro interno [{}]", errorId, ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "errorId", errorId,
                "message", "Erro interno do servidor"
        ));
    }
}
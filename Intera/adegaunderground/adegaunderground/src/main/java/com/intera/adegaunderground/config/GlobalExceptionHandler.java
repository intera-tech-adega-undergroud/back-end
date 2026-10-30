package com.intera.adegaunderground.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<Map<String, Object>> handleQuantityOverflow(ArithmeticException ex) {
        logger.warn("request_rejected reason=quantity_overflow");
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now().toString(),
                "message", "Quantidade fora do intervalo permitido."
        ));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            ServletRequestBindingException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpRequestMethodNotSupportedException.class,
            NoResourceFoundException.class,
            MultipartException.class
    })
    public ResponseEntity<Map<String, Object>> handleRequestError(Exception ex) {
        HttpStatusCode status = ex instanceof ErrorResponse error
                ? error.getStatusCode() : HttpStatus.BAD_REQUEST;
        String message;
        if (ex instanceof HttpMessageNotReadableException) {
            message = "Envie um corpo JSON válido com os campos e tipos esperados.";
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            message = "Método HTTP não permitido para esta rota.";
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            message = "Content-Type não suportado para esta requisição.";
        } else if (ex instanceof NoResourceFoundException) {
            message = "Rota não encontrada.";
        } else if (ex instanceof MaxUploadSizeExceededException) {
            message = "Arquivo excede o tamanho permitido.";
        } else if (ex instanceof MultipartException) {
            message = "Envie multipart/form-data com o campo arquivo.";
        } else {
            message = "Parâmetro obrigatório ausente ou inválido: " + ex.getMessage();
        }
        logger.warn("request_rejected status={} type={}", status.value(), ex.getClass().getSimpleName());
        var response = ResponseEntity.status(status);
        if (ex instanceof ErrorResponse error) {
            response.headers(error.getHeaders());
        }
        return response.body(Map.of("timestamp", Instant.now().toString(), "message", message));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException ex) {
        String errorId = UUID.randomUUID().toString();
        logger.warn("data_conflict errorId={} type={}", errorId, ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", Instant.now().toString(),
                "errorId", errorId,
                "message", "Operação em conflito com os dados existentes. Verifique duplicidades e vínculos."
        ));
    }

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now().toString(),
                "message", mensagem.isBlank() ? "Dados inválidos" : mensagem
        ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "timestamp", Instant.now().toString(),
                "message", ex.getReason() != null ? ex.getReason() : "Erro"
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

    @ExceptionHandler(
            org.springframework.web.method.annotation
                    .MethodArgumentTypeMismatchException.class
    )
    public ResponseEntity<Map<String, Object>> handleParameterTypeMismatch(
            org.springframework.web.method.annotation
                    .MethodArgumentTypeMismatchException ex
    ) {
        return ResponseEntity.badRequest().body(
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "message", "Parâmetro inválido: " + ex.getName()
                )
        );
    }
}
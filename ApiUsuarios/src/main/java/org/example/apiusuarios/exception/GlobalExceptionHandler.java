package org.example.apiusuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> body(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("fecha", LocalDateTime.now());
        cuerpo.put("mensaje", mensaje);
        return new ResponseEntity<>(cuerpo, status);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Object> manejarAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        return body(HttpStatus.FORBIDDEN, "Acceso denegado: " + ex.getMessage());
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<Object> manejarAuthentication(org.springframework.security.core.AuthenticationException ex) {
        return body(HttpStatus.UNAUTHORIZED, "No autenticado: " + ex.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Object> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Object> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> manejarTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return body(HttpStatus.NOT_FOUND, "Identificador invalido");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> manejarValidacion(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Petición invalida");
        return body(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> manejarResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String mensaje = ex.getReason() != null ? ex.getReason() : "Error";
        return body(status, mensaje);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> manejarErroresGenerales(Exception ex) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
}

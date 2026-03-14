package com.banco.banking.api.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Este método atrapa específicamente los errores de @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    // Opcional: Atrapa errores de datos duplicados (como el email único)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleSqlExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Ya existe un registro con esos datos (DNI o Email duplicado)");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, String>> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage()); // Aquí viajará "Saldo no disponible"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
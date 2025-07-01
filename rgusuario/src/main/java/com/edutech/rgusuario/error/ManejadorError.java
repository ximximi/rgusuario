package com.edutech.rgusuario.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.edutech.rgusuario.model.ApiRespuesta;

@RestControllerAdvice
public class ManejadorError {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiRespuesta<String>> manejarRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiRespuesta<>(ex.getMessage(), null));
    }
}
package com.picpaysimplicado.infra;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import com.picpaysimplicado.dtos.ExceptionDTO;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity threatDuplicateEntry(DataIntegrityViolationException exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO("Usuário já cadastrado", 400);

        return ResponseEntity.badRequest().body(exceptionDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity threatEntity(EntityNotFoundException exception){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity threatExternalAPIs(HttpClientErrorException exception){
        ExceptionDTO exceptionDTO;
        HttpStatus status;

        if(exception.getStatusCode() == HttpStatus.FORBIDDEN){
            exceptionDTO = new ExceptionDTO("Transação não autorizada", 403);
            status = HttpStatus.FORBIDDEN;
           
        }  else {
            throw exception;
    
        }

        return ResponseEntity.status(status).body(exceptionDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity threatGeneralException(Exception exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage(), 500);

        return ResponseEntity.internalServerError().body(exceptionDTO);
    }

}

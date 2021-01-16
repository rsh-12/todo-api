package ru.example.todo.exception;
/*
 * Date: 1/14/21
 * Time: 10:42 AM
 * */

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ControllerAdvice
public class TodoObjectExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<CustomErrorResponse> handleException(TodoObjectException ex) {

        var error = new CustomErrorResponse();
        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError("Not found");
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorResponse> handleException(Exception ex) {

        var error = new CustomErrorResponse();
        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("Bad Request");
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorResponse> handleException(RuntimeException ex) {

        var error = new CustomErrorResponse();
        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("Bad Request");
        error.setMessage("Conversion error");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}

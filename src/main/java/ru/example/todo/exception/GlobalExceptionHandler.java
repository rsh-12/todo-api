package ru.example.todo.exception;
/*
 * Date: 1/14/21
 * Time: 10:42 AM
 * */

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        Map<String, Set<String>> errorsMap = fieldErrors.stream().collect(
                Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())));

        return new ResponseEntity<>(errorsMap.isEmpty() ? ex : errorsMap, headers, status);
    }


    @ExceptionHandler
    public ResponseEntity<CustomErrorResponse> handleException(CustomException ex) {

        HttpStatus exHttpStatus = ex.getHttpStatus();

        if (exHttpStatus == null) {
            exHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        var error = new CustomErrorResponse();
        error.setTimestamp(new Date());
        error.setStatus(exHttpStatus.value());
        error.setError("Something went wrong");
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, exHttpStatus);
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

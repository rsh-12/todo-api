package ru.example.todoapp.exception;
/*
 * Date: 1/14/21
 * Time: 10:42 AM
 * */

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        Map<String, Set<String>> errorsMap = fieldErrors.stream().collect(
                Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())));

        return new ResponseEntity<>(errorsMap.isEmpty() ? ex : errorsMap, headers, status);
    }

    @ExceptionHandler
    public ResponseEntity<CustomErrorResponse> handleException(CustomException ex) {

        var error = new CustomErrorResponse.Builder()
                .status(ex.getHttpStatus())
                .error(ex.getError())
                .message(ex.getMessage()).build();

        return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatus()));
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorResponse> handleException(RuntimeException ex) {

        var error = new CustomErrorResponse.Builder()
                .status(HttpStatus.BAD_REQUEST)
                .error("Bad Request")
                .message("Conversion error").build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex) {

        var error = new CustomErrorResponse.Builder()
                .status(HttpStatus.FORBIDDEN)
                .error("Forbidden")
                .message("Not enough permissions").build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNotFoundException(NotFoundException ex) {
        return createResponse(ex);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomErrorResponse> handleBadRequestException(BadRequestException ex) {
        return createResponse(ex);
    }

    private <T extends ICustomException> ResponseEntity<CustomErrorResponse> createResponse(T ex) {
        var error = new CustomErrorResponse.Builder()
                .status(ex.getHttpStatus())
                .message(ex.getMessage()).build();

        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

}

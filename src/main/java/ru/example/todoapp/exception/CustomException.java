package ru.example.todoapp.exception;
/*
 * Date: 1/13/21
 * Time: 9:48 PM
 * */

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final String error;
    private final String message;
    private final HttpStatus httpStatus;


    public CustomException(String message, HttpStatus httpStatus) {
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.httpStatus = httpStatus;
    }    

    public static CustomException createForbiddenExc(String message) {
        return new CustomException(message, HttpStatus.FORBIDDEN);
    }

    public static CustomException createInternalServerErrorExc(String message) {
        return new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public String getError() {
        return error;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

package ru.example.todoapp.exception;
/*
 * Date: 14.03.2022
 * Time: 9:25 AM
 * */

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException implements ICustomException {

    public BadRequestException(String message) {
        super(message);
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}

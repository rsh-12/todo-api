package ru.example.todoapp.exception;
/*
 * Date: 14.03.2022
 * Time: 9:25 AM
 * */

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException implements ICustomException {

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}

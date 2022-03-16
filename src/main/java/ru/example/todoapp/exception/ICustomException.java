package ru.example.todoapp.exception;

import org.springframework.http.HttpStatus;

// todo: delete CustomException class and rename this interface to CustomException
public interface ICustomException {

    HttpStatus getHttpStatus();

    String getMessage();

}

package ru.example.todo.exception;
/*
 * Date: 1/13/21
 * Time: 9:48 PM
 * */

public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }
}

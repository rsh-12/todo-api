package ru.example.todo.exception;
/*
 * Date: 1/13/21
 * Time: 9:48 PM
 * */

public class TodoObjectNotFoundException extends RuntimeException {

    public TodoObjectNotFoundException(String message) {
        super(message);
    }

    public TodoObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TodoObjectNotFoundException(Throwable cause) {
        super(cause);
    }
}

package ru.example.todo.exception;
/*
 * Date: 1/13/21
 * Time: 9:48 PM
 * */

public class TodoObjectException extends RuntimeException {

    public TodoObjectException(String message) {
        super(message);
    }

    public TodoObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public TodoObjectException(Throwable cause) {
        super(cause);
    }
}

package ru.example.todo.exception;
/*
 * Date: 1/13/21
 * Time: 9:48 PM
 * */

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotFoundException(Throwable cause) {
        super(cause);
    }
}

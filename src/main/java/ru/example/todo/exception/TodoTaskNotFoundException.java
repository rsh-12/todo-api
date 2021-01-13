package ru.example.todo.exception;
/*
 * Date: 1/13/21
 * Time: 9:48 PM
 * */

public class TodoTaskNotFoundException extends RuntimeException {

    public TodoTaskNotFoundException(Long id) {
        super("Could not find task: " + id);
    }

}

package ru.example.todo.service;

import ru.example.todo.entity.TodoTask;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TodoTaskService {


    List<TodoTask> getListOfAllTodos();

    Optional<TodoTask> getOne(Long id);
}

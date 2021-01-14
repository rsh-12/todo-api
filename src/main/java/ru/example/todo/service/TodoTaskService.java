package ru.example.todo.service;

import ru.example.todo.entity.TodoTask;

import java.util.List;
import java.util.Optional;

public interface TodoTaskService {


    List<TodoTask> getAllTasks();

    Optional<TodoTask> getTaskById(Long id);

    void deleteTaskById(Long id);

    boolean existsById(Long id);
}

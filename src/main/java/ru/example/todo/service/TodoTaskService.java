package ru.example.todo.service;

import ru.example.todo.entity.TodoTask;

import java.util.List;

public interface TodoTaskService {

    List<TodoTask> getAllTasks();

    TodoTask getTaskById(Long id);

    void deleteTaskById(Long id);

    void save(TodoTask task);

    void createTask(TodoTask newTask);

    void updateTask(TodoTask patch, Long id);
}

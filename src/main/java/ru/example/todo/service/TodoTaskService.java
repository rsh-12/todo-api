package ru.example.todo.service;

import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.TaskStatus;

import java.util.List;

public interface TodoTaskService {

    List<TodoTask> getAllTasks();

    TodoTask getTaskById(Long id);

    void deleteTaskById(Long id);

    void createTask(TodoTask newTask);

    void updateTask(TodoTask patch, Long id);

    void setTaskStatus(Long taskId, TaskStatus completed, TaskStatus starred);
}

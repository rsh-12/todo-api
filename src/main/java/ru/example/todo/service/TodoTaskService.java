package ru.example.todo.service;

import ru.example.todo.domain.TodoTaskRequest;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.TaskDate;
import ru.example.todo.enums.TaskStatus;

import java.util.List;
import java.util.Set;

public interface TodoTaskService {

    List<TodoTask> getAllTasks(Integer pageNo, Integer pageSize, TaskDate date, String sort);

    TodoTask getTaskById(Long id);

    void deleteTaskById(Long id);

    void createTask(TodoTaskRequest todoTaskRequest);

    void updateTask(Long id, TodoTaskRequest todoTaskRequest, TaskStatus completed, TaskStatus starred);

    List<TodoTask> findAllBySetId(Set<Long> taskIds);

}

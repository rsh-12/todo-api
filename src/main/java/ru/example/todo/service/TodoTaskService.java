package ru.example.todo.service;

import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByDate;

import java.util.List;
import java.util.Set;

public interface TodoTaskService {

    List<TodoTask> findTasks(Long userId, Integer pageNo, Integer pageSize,
                             FilterByDate date, String sort);

    TodoTask findTaskById(Long userId, Long taskId);

    void deleteTaskById(User principal, Long taskId);

    TodoTask createTask(User user, TodoTask task);

    List<TodoTask> findTasksByIds(Set<Long> taskIds, Long userId);

    void save(TodoTask task);
}

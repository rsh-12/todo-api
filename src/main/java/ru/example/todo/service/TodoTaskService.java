package ru.example.todo.service;

import ru.example.todo.dto.TodoTaskDto;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.TaskDate;
import ru.example.todo.enums.TaskStatus;

import java.util.List;
import java.util.Set;

public interface TodoTaskService {

    List<TodoTask> getAllTasks(User user, Integer pageNo, Integer pageSize, TaskDate date, String sort);

    TodoTask getTaskById(User user, Long taskId);

    void deleteTaskById(User user, Long taskId);

    void createTask(User user, TodoTask task);

    void updateTask(User user, Long sectionId, TodoTaskDto task, TaskStatus completed, TaskStatus starred);

    List<TodoTask> findAllBySetId(Set<Long> taskIds, Long userId);

}

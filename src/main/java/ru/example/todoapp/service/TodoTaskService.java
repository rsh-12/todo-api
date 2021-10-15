package ru.example.todoapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.example.todoapp.domain.request.TaskIdsWrapper;
import ru.example.todoapp.domain.request.TodoTaskRequest;
import ru.example.todoapp.dto.TodoTaskDto;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.util.filters.FilterByDate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This interface consits methods for working with TodoTask objects.
 *
 * @see TodoTask
 */
public interface TodoTaskService {

    Page<TodoTask> findAll(FilterByDate date, Pageable pageable);

    /**
     * Finds the TodoTask by id and user id.
     *
     * @param taskId the TodoTask id
     * @return the TodoTask by id
     * @throws ru.example.todoapp.exception.CustomException if the TodoTask by id is not found
     * @see User
     * @see TodoTask
     */
    Optional<TodoTask> findTaskById(Long taskId);

    /**
     * Deletes the user's task by id
     * if the aspect class does not throw an exception.
     *
     * @param taskId the TodoTask id
     * @see User
     * @see TodoTask
     */
    void deleteTaskById(Long taskId);

    /**
     * Creates a new TodoTask object with the user, saves to database.
     *
     * @param taskRequest the TodoTask instance
     * @return the created TodoTask
     * @see User
     * @see TodoTask
     */
    TodoTask createTask(TodoTaskRequest taskRequest);

    /**
     * Finds TodoTask objects by set of provided ids and user id.
     *
     * @param taskIds the set of TodoTask ids
     * @param userId  the User id
     * @return the list of TodoTask objects
     * @see User
     * @see TodoTask
     * @see TaskIdsWrapper
     */
    List<TodoTask> findTasksByIds(Set<Long> taskIds, Long userId);

    /**
     * Saves the TodoTask object to database.
     *
     * @param taskRequest the TodoTaskRequest instance
     * @see TodoTask
     */
    Optional<TodoTask> saveTask(Long taskId, TodoTaskRequest taskRequest);

    TodoTaskDto mapToTaskDto(TodoTask task);
}

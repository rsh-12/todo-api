package ru.example.todo.service;

import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByDate;

import java.util.List;
import java.util.Set;

/**
 * This interface consits methods for working with TodoTask objects.
 *
 * @see TodoTask
 */
public interface TodoTaskService {

    /**
     * Returns a list of TodoTask objects with some applied filters.
     * By default, it returns 10 objects sorted by date(ALL).
     *
     * @param userId   the User id
     * @param pageNo   the page number
     * @param pageSize the number of objects to return
     * @param date     the date filter: overdue, today, all
     * @param sort     the sorting by field name, by default sorts in desc order
     * @return the list
     * @see User
     * @see TodoTask
     * @see FilterByDate
     */
    List<TodoTask> findTasks(Long userId, Integer pageNo, Integer pageSize, FilterByDate date, String sort);

    /**
     * Finds the TodoTask by id and user id.
     *
     * @param userId the User id
     * @param taskId the TodoTask id
     * @return the TodoTask by id
     * @throws ru.example.todo.exception.CustomException if the TodoTask by id is not found
     * @see User
     * @see TodoTask
     */
    TodoTask findTaskById(Long userId, Long taskId);

    /**
     * Deletes the user's task by id
     * if the aspect class does not throw an exception.
     *
     * @param principal the User instance
     * @param taskId    the TodoTask id
     * @see User
     * @see TodoTask
     * @see ru.example.todo.aop.ValidatingAspect
     */
    void deleteTaskById(User principal, Long taskId);

    /**
     * Creates a new TodoTask object with the user, saves to database.
     *
     * @param user the User instance
     * @param task the TodoTask instance
     * @return the created TodoTask
     * @see User
     * @see TodoTask
     */
    TodoTask createTask(User user, TodoTask task);

    /**
     * Finds TodoTask objects by set of provided ids and user id.
     *
     * @param taskIds the set of TodoTask ids
     * @param userId  the User id
     * @return the list of TodoTask objects
     * @see User
     * @see TodoTask
     * @see ru.example.todo.controller.wrapper.TaskIdsWrapper
     */
    List<TodoTask> findTasksByIds(Set<Long> taskIds, Long userId);

    /**
     * Saves the TodoTask object to database.
     *
     * @param task the TodoTask instance
     * @see TodoTask
     */
    void saveTodoTask(TodoTask task);

}

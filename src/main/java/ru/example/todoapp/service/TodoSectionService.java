package ru.example.todoapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.example.todoapp.domain.request.TodoSectionRequest;
import ru.example.todoapp.dto.TodoSectionDto;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.util.filters.FilterByOperation;

import java.util.List;
import java.util.Optional;

/**
 * This interface contains methods for working with TodoSection.
 *
 * @see TodoSection
 */
public interface TodoSectionService {

    /**
     * Finds a section by id.
     *
     * @param id the TodoSection id
     * @return the TodoSection
     * @throws ru.example.todoapp.exception.CustomException if the TodoSection is not found
     * @see TodoSection
     */
    Optional<TodoSection> findOne(Long id);

    Page<TodoSection> findAll(Pageable pageable);

    /**
     * Deletes the TodoSection by id. Checks if the Principal
     * equals to User of the TodoSection or Principal has an Admin role,
     * then deletes the TodoSection or throws an exception.
     *
     * @param id the TodoSection id
     * @throws ru.example.todoapp.exception.CustomException if the TodoSection by id is not found
     *                                                      or if the Principal is not equal to the User
     *                                                      of the TodoSeciton and the Principal does not have
     *                                                      an Admin role
     * @see User
     */
    void delete(Long id);

    /**
     * Creates a new TodoSection.
     *
     * @param request the TodoSection
     * @return the created TodoSection
     * @see TodoSection
     * @see User
     */
    TodoSection create(TodoSectionRequest request);

    /**
     * Updates the TodoSection by id.
     *
     * @param request the TodoSection
     * @return the updated TodoSeciton
     * @throws ru.example.todoapp.exception.CustomException if the TodoSection by id is not found
     *                                                      or if the Principal is not equal to the User
     *                                                      of the TodoSeciton and the Principal does not have
     *                                                      an Admin role
     * @see TodoSection
     */
    Optional<TodoSection> update(Long sectionId, TodoSectionRequest request);

    /**
     * Add tasks or removes them from the TodoSection object.
     *
     * @param userId    the User id
     * @param sectionId the TodoSection id
     * @param tasks     the list of tasks
     * @param flag      the flag that contains <b>move</b> or <b>remove</b>
     * @throws ru.example.todoapp.exception.CustomException if the TodoSection by id and userId is not found
     * @see TodoTask
     * @see TodoSection
     */
    void addTasksToOrRemoveFromSection(Long userId, Long sectionId, List<TodoTask> tasks, FilterByOperation flag);

    TodoSectionDto mapToSectionDto(TodoSection section);

}

package ru.example.todoapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.example.todoapp.controller.request.TodoSectionRequest;
import ru.example.todoapp.dto.TodoSectionDto;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.enums.filters.FilterByOperation;
import ru.example.todoapp.repository.projection.TodoSectionProjection;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This interface contains methods for working with TodoSection.
 *
 * @see TodoSection
 */
public interface TodoSectionService {

    /**
     * Finds a section by id.
     *
     * @param sectionId the TodoSection id
     * @return the TodoSection
     * @throws ru.example.todoapp.exception.CustomException if the TodoSection is not found
     * @see TodoSection
     */
    Optional<TodoSection> findSectionById(Long sectionId);

    /**
     * Finds specific fields of the TodoSection by user id:
     * id, title, createdAt, updatedAt.
     *
     * @return the TodoSectionProjection list
     * @see TodoSectionProjection
     * @see TodoSection
     */
    Page<TodoSectionDto> findSections(Pageable pageable);

    /**
     * Deletes the TodoSection by id. Checks if the Principal
     * equals to User of the TodoSection or Principal has an Admin role,
     * then deletes the TodoSection or throws an exception.
     *
     * @param sectionId the TodoSection id
     * @throws ru.example.todoapp.exception.CustomException if the TodoSection by id is not found
     *                                                      or if the Principal is not equal to the User
     *                                                      of the TodoSeciton and the Principal does not have
     *                                                      an Admin role
     * @see User
     */
    void deleteSectionById(Long sectionId);

    /**
     * Creates a new TodoSection.
     *
     * @param sectionRequest the TodoSection
     * @return the created TodoSection
     * @see TodoSection
     * @see User
     */
    TodoSection createSection(TodoSectionRequest sectionRequest);

    /**
     * Updates the TodoSection by id.
     *
     * @param sectionId      the TodoSection id
     * @param sectionRequest the TodoSection
     * @return the updated TodoSeciton
     * @throws ru.example.todoapp.exception.CustomException if the TodoSection by id is not found
     *                                                      or if the Principal is not equal to the User
     *                                                      of the TodoSeciton and the Principal does not have
     *                                                      an Admin role
     * @see TodoSection
     */
    TodoSection updateSection(Long sectionId, TodoSectionRequest sectionRequest);

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

    TodoSectionDto mapToSectionDto(TodoSectionProjection projection);

    TodoSectionDto mapToSectionDto(TodoSection section);

    <T> TodoSectionDto mapToSectionDto(T t, Function<T, TodoSectionDto> f);

}

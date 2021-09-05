package ru.example.todoapp.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.example.todoapp.controller.request.TodoTaskRequest;
import ru.example.todoapp.dsl.TaskBuilder;
import ru.example.todoapp.dto.TodoTaskDto;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.enums.filters.FilterByDate;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoTaskRepository;
import ru.example.todoapp.service.TodoTaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static ru.example.todoapp.enums.Role.ADMIN;
import static ru.example.todoapp.enums.filters.FilterByDate.ALL;
import static ru.example.todoapp.enums.filters.FilterByDate.TODAY;

@Service
public class TodoTaskServiceImpl implements TodoTaskService {

    private static final Logger log = LoggerFactory.getLogger(TodoTaskServiceImpl.class);

    private final TodoTaskRepository todoTaskRepository;
    private final AuthUserFacade authUserFacade;

    @Autowired
    public TodoTaskServiceImpl(TodoTaskRepository todoTaskRepository, AuthUserFacade authUserFacade) {
        this.todoTaskRepository = todoTaskRepository;
        this.authUserFacade = authUserFacade;
    }

    // get all tasks
    @Override
    public Page<TodoTask> findTasks(FilterByDate date, Pageable pageable) {
        Long userId = authUserFacade.getUserId();

        return date == ALL
                ? todoTaskRepository.findAllByUserId(userId, pageable)
                : findTasksByDate(date, pageable, userId);
    }

    private Page<TodoTask> findTasksByDate(FilterByDate date, Pageable pageable, Long userId) {
        if (date == TODAY) {
            return todoTaskRepository.findAllByCompletionDateEqualsAndUserId(LocalDate.now(), userId, pageable);
        }
        return todoTaskRepository.findAllByCompletionDateBeforeAndUserId(LocalDate.now(), userId, pageable);
    }

    // get task by id
    @Override
    public Optional<TodoTask> findTaskById(Long taskId) {
        log.info("Get the task by id: {}", taskId);
        return todoTaskRepository.findByIdAndUserId(taskId, authUserFacade.getUserId());
    }

    // delete task by id
    @Override
    public void deleteTaskById(Long taskId) {
        Predicate<User> predicate = user -> user.equals(authUserFacade.getLoggedUser());
        Predicate<User> combinedPredicates = predicate.or(user -> user.getRoles().contains(ADMIN));

        todoTaskRepository.findById(taskId).map(TodoTask::getUser)
                .filter(combinedPredicates)
                .ifPresentOrElse(user -> todoTaskRepository.deleteById(taskId), () -> {
                    throw CustomException.notFound("Task not found");
                });
    }

    // create new task
    @Override
    public TodoTask createTask(TodoTaskRequest request) {
        log.info("Create a new task");

        TodoTask todoTask = TaskBuilder.forTask(request.title())
                .starred(request.starred())
                .completionDate(request.completionDate())
                .user(authUserFacade.getLoggedUser())
                .build();

        return todoTaskRepository.save(todoTask);
    }

    @Override
    public List<TodoTask> findTasksByIds(Set<Long> taskIds, Long userId) {
        List<TodoTask> tasksByIds = todoTaskRepository.findAllByIdInAndUserId(taskIds, userId);
        log.info("Get tasks by set of ids: {}", tasksByIds.size());
        return tasksByIds;
    }

    @Override
    public Optional<TodoTask> saveTask(Long taskId, TodoTaskRequest request) {

        return findTaskById(taskId)
                .map(task -> {
                    task.setTitle(request.title());
                    task.setStarred(request.starred());
                    task.setCompletionDate(request.completionDate());
                    return todoTaskRepository.save(task);
                });
    }

    @Override
    public TodoTaskDto mapToTaskDto(TodoTask task) {
        return new TodoTaskDto(
                task.getId(),
                task.getTitle(),
                task.getCompletionDate(),
                task.isCompleted(),
                task.isStarred(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

}

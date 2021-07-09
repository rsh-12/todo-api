package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByDate;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoTaskRepository;
import ru.example.todo.service.TodoTaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class TodoTaskServiceImpl extends AbstractServiceClass implements TodoTaskService {

    private static final Logger log = LoggerFactory.getLogger(TodoTaskServiceImpl.class.getName());

    private final TodoTaskRepository todoTaskRepository;

    @Autowired
    public TodoTaskServiceImpl(TodoTaskRepository todoTaskRepository) {
        this.todoTaskRepository = todoTaskRepository;
    }

    // get all tasks
    @Override
    public List<TodoTask> findTasks(Long userId, Integer pageNo, Integer pageSize, FilterByDate date, String sort) {
        pageSize = pageSize > 100 ? 100 : pageSize; // set max page size
        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by(getSortDirection(sort), getSortAsString(sort)));

        switch (date) {
            case TODAY:
                log.info("Get today's tasks");
                return todoTaskRepository.findAllByCompletionDateEqualsAndUserId(LocalDate.now(), page, userId);
            case OVERDUE:
                log.info("Get overdue tasks");
                return todoTaskRepository.findAllByCompletionDateBeforeAndUserId(LocalDate.now(), page, userId);
            default:
                log.info("Get all tasks");
                return todoTaskRepository.findAllByUserId(userId, page);
        }

    }

    // get task by id
    @Override
    public TodoTask findTaskById(Long userId, Long taskId) {
        log.info("Get the task by id: {}", taskId);
        return todoTaskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new CustomException("Task not found: " + taskId, HttpStatus.NOT_FOUND));
    }

    // delete task by id
    @Override
    public void deleteTaskById(User principal, Long taskId) {
        TodoTask task = todoTaskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException("Task not found: " + taskId, HttpStatus.NOT_FOUND));

        if (isUserValidOrHasRoleAdmin(principal, task.getUser())) {
            todoTaskRepository.deleteById(taskId);
        } else {
            throw new CustomException("Not enough permissions", HttpStatus.FORBIDDEN);
        }

        log.info("The task with id={} was deleted successfully", taskId);
    }

    // create new task
    @Override
    public TodoTask createTask(User user, TodoTask task) {
        log.info("Create a new task");
        task.setUser(user);
        return todoTaskRepository.save(task);
    }

    @Override
    public List<TodoTask> findTasksByIds(Set<Long> taskIds, Long userId) {
        List<TodoTask> tasksByIds = todoTaskRepository.findAllByIdInAndUserId(taskIds, userId);
        log.info("Get tasks by set of ids: {}", tasksByIds.size());
        return tasksByIds;
    }

    @Override
    public void save(TodoTask task) {
        todoTaskRepository.save(task);
    }

}

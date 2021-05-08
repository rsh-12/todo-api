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
import ru.example.todo.dto.TodoTaskDto;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.TaskDate;
import ru.example.todo.enums.TaskStatus;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoTaskRepository;
import ru.example.todo.service.TodoTaskService;

import java.time.LocalDate;
import java.util.Date;
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
    public List<TodoTask> getAllTasks(User user, Integer pageNo, Integer pageSize, TaskDate date, String sort) {

        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by(getSortDirection(sort), getSortAsString(sort)));

        if (date.equals(TaskDate.TODAY)) {
            log.info("Get today's tasks");
            return todoTaskRepository.findAllByCompletionDateEqualsAndUserId(LocalDate.now(), page, user.getId());
        } else if (date.equals(TaskDate.OVERDUE)) {
            log.info("Get overdue tasks");
            return todoTaskRepository.findAllByCompletionDateBeforeAndUserId(LocalDate.now(), page, user.getId());
        }

        log.info("Get all tasks");
        return todoTaskRepository.findAllByUserId(user.getId(), page).getContent();
    }

    // get task by id
    @Override
    public TodoTask getTaskById(User user, Long taskId) {
        log.info("Get the task by id: {}", taskId);
        return todoTaskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new CustomException("Not Found", "Task not found: " + taskId, HttpStatus.NOT_FOUND));
    }

    // delete task by id
    @Override
    public void deleteTaskById(User user, Long taskId) {

        TodoTask task = todoTaskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException("Not Found", "Task not found: " + taskId, HttpStatus.NOT_FOUND));

        if (isValidOrAdmin(user, task.getUser())) {
            todoTaskRepository.deleteById(taskId);
        } else {
            throw new CustomException("Forbidden", "Not enough permissions", HttpStatus.FORBIDDEN);
        }

        log.info("The task with id={} was deleted successfully", taskId);
    }

    // create new task
    @Override
    public void createTask(User user, TodoTask task) {
        log.info("Create a new task");
        task.setUser(user);
        todoTaskRepository.save(task);
    }

    @Override
    public List<TodoTask> findAllBySetId(Set<Long> taskIds, Long userId) {
        List<TodoTask> tasksByIds = todoTaskRepository.findAllByIdInAndUserId(taskIds, userId);
        log.info("Get tasks by set of ids: {}", tasksByIds.size());
        return tasksByIds;
    }

    // update task by id
    @Override
    public void updateTask(User user, Long taskId, TodoTaskDto task,
                           TaskStatus completed, TaskStatus starred) {

        // get task from DB
        log.info("Get the task from DB: id={}", taskId);
        TodoTask taskFromDB = todoTaskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new CustomException("Not Found", "Task not found: " + taskId, HttpStatus.NOT_FOUND));

        // update task title or task completion date
        if (task != null) {
            setTitleOrDate(task, taskFromDB);
        }

        // set task status
        log.info("Update task 'completed' field");
        if (completed != null) taskFromDB.setCompleted(toABoolean(completed));

        log.info("Update task 'starred' field");
        if (starred != null) taskFromDB.setStarred(toABoolean(starred));

        log.info("Update task 'updatedAt' field");
        taskFromDB.setUpdatedAt(new Date());

        log.info("Save the updated task: id={}", taskId);
        todoTaskRepository.save(taskFromDB);
    }

}

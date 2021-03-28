package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.modelmapper.ModelMapper;
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
public class TodoTaskServiceImpl implements TodoTaskService {

    private static final Logger log = LoggerFactory.getLogger(TodoTaskServiceImpl.class.getName());

    private final TodoTaskRepository todoTaskRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TodoTaskServiceImpl(TodoTaskRepository todoTaskRepository, ModelMapper modelMapper) {
        this.todoTaskRepository = todoTaskRepository;
        this.modelMapper = modelMapper;
    }

    // get all tasks
    @Override
    public List<TodoTask> getAllTasks(Integer pageNo, Integer pageSize, TaskDate date, String sort) {

        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by(getSortDirection(sort), getSortAsString(sort)));

        if (date.equals(TaskDate.TODAY)) {
            log.info("Get today's tasks");
            return todoTaskRepository.findAllByCompletionDateEquals(LocalDate.now(), page);
        } else if (date.equals(TaskDate.OVERDUE)) {
            log.info("Get overdue tasks");
            return todoTaskRepository.findAllByCompletionDateBefore(LocalDate.now(), page);
        }

        log.info("Get all tasks");
        return todoTaskRepository.findAll(page).getContent();
    }

    // get task by id
    @Override
    public TodoTask getTaskById(Long id) {
        log.info("Get the task by id: {}", id);
        return todoTaskRepository.findById(id)
                .orElseThrow(() -> new CustomException("Task not found: " + id, HttpStatus.NOT_FOUND));
    }

    // delete task by id
    @Override
    public void deleteTaskById(Long id) {
        if (!todoTaskRepository.existsById(id)) {
            throw new CustomException("Task not found: " + id, HttpStatus.NOT_FOUND);
        }

        todoTaskRepository.deleteById(id);
        log.info("The task with id={} was deleted successfully", id);
    }

    // create new task
    @Override
    public void createTask(TodoTask task) {
        log.info("Create a new task");
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
    public void updateTask(Long id, TodoTaskDto task,
                           TaskStatus completed, TaskStatus starred) {

        // get task from DB
        log.info("Get the task from DB: id={}", id);
        TodoTask taskFromDB = todoTaskRepository.findById(id)
                .orElseThrow(() -> new CustomException("Task not found: " + id, HttpStatus.NOT_FOUND));

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

        log.info("Save the updated task: id={}", id);
        todoTaskRepository.save(taskFromDB);
    }

    private void setTitleOrDate(TodoTaskDto taskDto, TodoTask taskFromDB) {
        log.info("Update task 'completionDate' field");
        if (taskDto.getCompletionDate() != null) taskFromDB.setCompletionDate(taskDto.getCompletionDate());

        log.info("Update task 'title' field");
        if (taskDto.getTitle() != null) taskFromDB.setTitle(taskDto.getTitle());
    }

    // --------------------------------------------------------------------------- Helper methods.
    private boolean toABoolean(TaskStatus status) {
        return Boolean.parseBoolean(status.toString());
    }

    private Sort.Direction getSortDirection(String sort) {
        if (sort.contains(",asc")) return Sort.Direction.ASC;
        return Sort.Direction.DESC;
    }

    private String getSortAsString(String sort) {
        if (sort.contains(",")) return sort.split(",")[0];
        return sort;
    }

}

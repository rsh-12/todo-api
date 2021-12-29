package ru.example.todoapp.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import static ru.example.todoapp.util.filters.FilterByDate.OVERDUE;
import static ru.example.todoapp.util.filters.FilterByDate.TODAY;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.todoapp.domain.request.TodoTaskRequest;
import ru.example.todoapp.dsl.TaskBuilder;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoTaskRepository;
import ru.example.todoapp.service.TodoTaskService;
import ru.example.todoapp.service.UserService;
import ru.example.todoapp.util.ServiceUtil;
import ru.example.todoapp.util.filters.FilterByDate;

@Service
@Transactional
public class TodoTaskServiceImpl implements TodoTaskService {

    private static final Logger log = LoggerFactory.getLogger(TodoTaskServiceImpl.class);

    private final TodoTaskRepository taskRepository;
    private final AuthUserFacade currentUser;
    private final UserService userService;
    private final ServiceUtil serviceUtil;

    @Autowired
    public TodoTaskServiceImpl(
            TodoTaskRepository taskRepository,
            AuthUserFacade currentUser,
            UserService userService,
            ServiceUtil serviceUtil) {

        this.taskRepository = taskRepository;
        this.currentUser = currentUser;
        this.userService = userService;
        this.serviceUtil = serviceUtil;
    }

    // get all tasks
    @Override
    @Transactional(readOnly = true)
    public Page<TodoTask> findAll(FilterByDate date, Pageable pageable) {
        Long userId = currentUser.getId();

        if (date == OVERDUE) {
            log.info("findAll: getting overdue tasks");
            return taskRepository.findAllByCompletionDateBeforeAndUserId(
                    LocalDate.now(), userId, pageable);
        } else if (date == TODAY) {
            log.info("findAll: getting current tasks");
            return taskRepository.findAllByCompletionDateEqualsAndUserId(
                    LocalDate.now(), userId, pageable);
        } else {
            log.info("findAll: getting all tasks");
            return taskRepository.findAllByUserId(userId, pageable);
        }
    }

    // get task by id
    @Override
    @Transactional(readOnly = true)
    public Optional<TodoTask> findOne(Long taskId) {
        log.info("findOne: getting the TodoTask with ID={}", taskId);

        return currentUser.containsRoleAdmin()
                ? taskRepository.findById(taskId)
                : taskRepository.findByIdAndUserId(taskId, currentUser.getId());
    }

    // delete task by id
    @Override
    public void delete(Long taskId) {
        log.info("delete: deleting a TodoTask with ID={}", taskId);

        taskRepository.findById(taskId)
                .map(TodoTask::getUserId)
                .filter(serviceUtil::ownerOrAdmin)
                .ifPresent(taskRepository::deleteById);
    }

    // create new task
    @Override
    public TodoTask create(TodoTaskRequest request) {
        log.info("create: creating a new TodoTask");

        TodoTask todoTask = TaskBuilder.forTask(request.title())
                .starred(request.starred())
                .completionDate(request.completionDate())
                .build();

        userService.findOne(currentUser.getId()).ifPresent(todoTask::setUser);

        return taskRepository.save(todoTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoTask> findByIds(Set<Long> taskIds, Long userId) {
        log.info("findByIds: getting tasks by IDS={}", taskIds);
        return taskRepository.findAllByIdInAndUserId(taskIds, userId);
    }

    @Override
    public Optional<TodoTask> update(Long taskId, TodoTaskRequest request) {
        log.info("update: updating the task with ID={}", taskId);
        return findOne(taskId)
                .map(task -> {
                    task.setTitle(request.title());
                    task.setStarred(request.starred());
                    task.setCompletionDate(request.completionDate());
                    return taskRepository.save(task);
                });
    }

}

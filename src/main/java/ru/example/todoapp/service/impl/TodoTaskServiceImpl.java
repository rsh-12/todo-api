package ru.example.todoapp.service.impl;
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
import org.springframework.stereotype.Service;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.enums.filters.FilterByDate;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoTaskRepository;
import ru.example.todoapp.service.TodoTaskService;
import ru.example.todoapp.service.impl.util.ServiceUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static ru.example.todoapp.enums.filters.FilterByDate.OVERDUE;
import static ru.example.todoapp.enums.filters.FilterByDate.TODAY;
import static ru.example.todoapp.service.impl.util.ServiceUtil.validateUser;

@Service
public class TodoTaskServiceImpl implements TodoTaskService {

    private static final Logger log = LoggerFactory.getLogger(TodoTaskServiceImpl.class.getName());

    private final TodoTaskRepository todoTaskRepository;
    private final AuthUserFacade authUserFacade;

    @Autowired
    public TodoTaskServiceImpl(TodoTaskRepository todoTaskRepository, AuthUserFacade authUserFacade) {
        this.todoTaskRepository = todoTaskRepository;
        this.authUserFacade = authUserFacade;
    }

    // get all tasks
    @Override
    public List<TodoTask> findTasks(Integer pageNo, Integer pageSize, FilterByDate date, String sort) {
        pageSize = pageSize > 100 ? 100 : pageSize; // set max page size
        Pageable page = PageRequest.of(pageNo, pageSize,
                Sort.by(ServiceUtil.getSortDirection(sort), ServiceUtil.getSortAsString(sort)));

        Long userId = authUserFacade.getUserId();
        if (date == TODAY) {
            return todoTaskRepository.findAllByCompletionDateEqualsAndUserId(LocalDate.now(), page, userId);
        } else if (date == OVERDUE) {
            return todoTaskRepository.findAllByCompletionDateBeforeAndUserId(LocalDate.now(), page, userId);
        }
        return todoTaskRepository.findAllByUserId(userId, page);
    }

    // get task by id
    @Override
    public TodoTask findTaskById(Long taskId) {
        log.info("Get the task by id: {}", taskId);
        return todoTaskRepository.findByIdAndUserId(taskId, authUserFacade.getUserId())
                .orElseThrow(() -> CustomException.notFound("Task Not Found"));
    }

    // delete task by id
    @Override
    public void deleteTaskById(Long taskId) {
        TodoTask task = todoTaskRepository.findById(taskId)
                .orElseThrow(() -> CustomException.notFound("Task not found: id=" + taskId));
        validateUser(authUserFacade.getLoggedUser(), task.getUser());

        todoTaskRepository.deleteById(taskId);
        log.info("The task with id={} was deleted successfully", taskId);
    }

    // create new task
    @Override
    public TodoTask createTask(TodoTask task) {
        log.info("Create a new task");
        task.setUser(authUserFacade.getLoggedUser());
        return todoTaskRepository.save(task);
    }

    @Override
    public List<TodoTask> findTasksByIds(Set<Long> taskIds, Long userId) {
        List<TodoTask> tasksByIds = todoTaskRepository.findAllByIdInAndUserId(taskIds, userId);
        log.info("Get tasks by set of ids: {}", tasksByIds.size());
        return tasksByIds;
    }

    @Override
    public void saveTodoTask(TodoTask task) {
        todoTaskRepository.save(task);
    }

}

package ru.example.todoapp.facade.impl;
/*
 * Date: 6/5/21
 * Time: 8:37 PM
 * */

import org.springframework.stereotype.Component;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.facade.TasksFacade;
import ru.example.todoapp.service.TodoSectionService;
import ru.example.todoapp.service.TodoTaskService;

import java.util.List;
import java.util.Set;

@Component
public class TasksFacadeImpl implements TasksFacade {

    private final TodoTaskService taskService;
    private final TodoSectionService sectionService;
    private final AuthUserFacade authUserFacade;

    public TasksFacadeImpl(TodoTaskService taskService, TodoSectionService sectionService, AuthUserFacade authUserFacade) {
        this.taskService = taskService;
        this.sectionService = sectionService;
        this.authUserFacade = authUserFacade;
    }

    @Override
    public void addTasks(Long sectionId, Set<Long> taskIds) {
        checkTaskIds(taskIds);
        Long userId = authUserFacade.getUserId();
        List<TodoTask> tasks = taskService.findByIds(taskIds, userId);
        sectionService.addTasks(userId, sectionId, tasks);
    }

    @Override
    public void removeTasks(Long sectionId, Set<Long> taskIds) {
        checkTaskIds(taskIds);
        Long userId = authUserFacade.getUserId();
        List<TodoTask> tasks = taskService.findByIds(taskIds, userId);
        sectionService.removeTasks(userId, sectionId, tasks);
    }

    private void checkTaskIds(Set<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            throw CustomException.createBadRequestExc("Tasks IDs are required");
        }
    }

}

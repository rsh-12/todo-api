package ru.example.todoapp.facade.impl;
/*
 * Date: 6/5/21
 * Time: 8:37 PM
 * */

import org.springframework.stereotype.Component;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.enums.filters.FilterByOperation;
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
    public void addTasksToOrRemoveFromSection(Long sectionId, Set<Long> taskIds, FilterByOperation flag) {
        if (taskIds == null || taskIds.isEmpty()) {
            throw CustomException.badRequest("Tasks IDs are required");
        }
        Long userId = authUserFacade.getUserId();
        List<TodoTask> tasks = taskService.findTasksByIds(taskIds, userId);
        sectionService.addTasksToOrRemoveFromSection(userId, sectionId, tasks, flag);
    }
}

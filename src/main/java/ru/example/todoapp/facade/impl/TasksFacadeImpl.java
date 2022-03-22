package ru.example.todoapp.facade.impl;
/*
 * Date: 6/5/21
 * Time: 8:37 PM
 * */

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.exception.BadRequestException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.facade.TasksFacade;
import ru.example.todoapp.service.TodoSectionService;
import ru.example.todoapp.service.TodoTaskService;

@Component
public class TasksFacadeImpl implements TasksFacade {

    private final TodoTaskService taskService;
    private final TodoSectionService sectionService;
    private final AuthUserFacade currentUser;

    public TasksFacadeImpl(
            TodoTaskService taskService,
            TodoSectionService sectionService,
            AuthUserFacade currentUser) {
        this.taskService = taskService;
        this.sectionService = sectionService;
        this.currentUser = currentUser;
    }

    @Override
    public void addTasks(Long sectionId, Set<Long> taskIds) {
        checkTaskIds(taskIds);
        Long userId = currentUser.getId();
        List<TodoTask> tasks = taskService.findByIds(taskIds, userId);
        sectionService.addTasks(userId, sectionId, tasks);
    }

    @Override
    public void removeTasks(Long sectionId, Set<Long> taskIds) {
        checkTaskIds(taskIds);
        Long userId = currentUser.getId();
        List<TodoTask> tasks = taskService.findByIds(taskIds, userId);
        sectionService.removeTasks(userId, sectionId, tasks);
    }

    private void checkTaskIds(Set<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            throw new BadRequestException("Tasks IDs are required");
        }
    }

}

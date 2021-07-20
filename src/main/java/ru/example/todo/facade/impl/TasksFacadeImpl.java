package ru.example.todo.facade.impl;
/*
 * Date: 6/5/21
 * Time: 8:37 PM
 * */

import org.springframework.stereotype.Component;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.filters.FilterByOperation;
import ru.example.todo.exception.CustomException;
import ru.example.todo.facade.TasksFacade;
import ru.example.todo.service.TodoSectionService;
import ru.example.todo.service.TodoTaskService;

import java.util.List;
import java.util.Set;

@Component
public class TasksFacadeImpl implements TasksFacade {

    private final TodoTaskService taskService;
    private final TodoSectionService sectionService;

    public TasksFacadeImpl(TodoTaskService taskService, TodoSectionService sectionService) {
        this.taskService = taskService;
        this.sectionService = sectionService;
    }


    @Override
    public void addTasksToOrRemoveFromSection(Long userId, Long sectionId, Set<Long> taskIds, FilterByOperation flag) {
        if (taskIds == null || taskIds.isEmpty()) {
            throw CustomException.badRequest("Tasks IDs are required");
        }
        List<TodoTask> tasks = taskService.findTasksByIds(taskIds, userId);
        sectionService.addTasksToOrRemoveFromSection(userId, sectionId, tasks, flag);
    }
}

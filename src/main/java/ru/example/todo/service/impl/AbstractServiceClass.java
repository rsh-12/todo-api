package ru.example.todo.service.impl;
/*
 * Date: 5/8/21
 * Time: 7:21 AM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.dto.TodoTaskDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;
import ru.example.todo.enums.SetTasks;
import ru.example.todo.enums.TaskStatus;
import ru.example.todo.service.JwtTokenService;

import java.util.List;

public class AbstractServiceClass {

    private static final Logger log = LoggerFactory.getLogger(AbstractServiceClass.class.getName());

    boolean toABoolean(TaskStatus status) {
        return Boolean.parseBoolean(status.toString());
    }

    Sort.Direction getSortDirection(String sort) {
        if (sort.contains(",asc")) return Sort.Direction.ASC;
        return Sort.Direction.DESC;
    }

    String getSortAsString(String sort) {
        if (sort.contains(",")) return sort.split(",")[0];
        return sort;
    }

    String buildResponseBody(User user, JwtTokenService jwtTokenService, TokenProperties tokenProperties) {

        String accessToken = jwtTokenService.buildAccessToken(user.getUsername(), user.getRoles());
        String refreshToken = jwtTokenService.buildRefreshToken(user.getUsername()).getToken();

        return String.format("{\"access_token\": \"%s\", " +
                        "\"refresh_token\": \"%s\", " +
                        "\"token_type\": \"Bearer\", " +
                        "\"expires\": %d}",
                accessToken, refreshToken, tokenProperties.getAccessTokenValidity());
    }

    void addOrRemoveTasks(SetTasks flag, TodoSection section, List<TodoTask> tasksByIds) {

        if (flag.equals(SetTasks.MOVE)) {
            log.info("Add tasks to the section");
            section.setTodoTasks(tasksByIds);
        } else if (flag.equals(SetTasks.REMOVE)) {
            log.info("Remove tasks from the section");
            section.removeTodoTasks(tasksByIds);
        }

    }

    void setTitleOrDate(TodoTaskDto taskDto, TodoTask taskFromDB) {
        log.info("Update task 'completionDate' field");
        if (taskDto.getCompletionDate() != null) taskFromDB.setCompletionDate(taskDto.getCompletionDate());

        log.info("Update task 'title' field");
        if (taskDto.getTitle() != null) taskFromDB.setTitle(taskDto.getTitle());
    }

    <T> boolean isValidOrAdmin(User user, T entity) {
        return (entity != null && entity.equals(user)) ||
                user.getRoles().contains(Role.ROLE_ADMIN);
    }

}

package ru.example.todo.service.impl;
/*
 * Date: 5/8/21
 * Time: 7:21 AM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Sort;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.dto.TodoTaskDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;
import ru.example.todo.enums.filters.FilterByOperation;
import ru.example.todo.enums.filters.FilterByBoolean;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.JwtTokenService;

import java.util.List;
import java.util.Optional;

public class AbstractServiceClass {

    private static final Logger log = LoggerFactory.getLogger(AbstractServiceClass.class.getName());

    boolean toABoolean(FilterByBoolean status) {
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

        JSONObject response = new JSONObject();
        try {
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken);
            response.put("token_type", "Bearer");
            response.put("access_token_expires", tokenProperties.getAccessTokenValidity());
            response.put("refresh_token_expires", tokenProperties.getRefreshTokenValidity());
        } catch (JSONException e) {
            throw new CustomException("Error during building response");
        }
        return response.toString();
    }

    void addOrRemoveTasks(FilterByOperation flag, TodoSection section, List<TodoTask> tasksByIds) {

        if (flag.equals(FilterByOperation.MOVE)) {
            log.info("Add tasks to the section");
            section.setTodoTasks(tasksByIds);
        } else if (flag.equals(FilterByOperation.REMOVE)) {
            log.info("Remove tasks from the section");
            section.removeTodoTasks(tasksByIds);
        }

    }

    void setTitleOrDate(TodoTaskDto taskDto, TodoTask task) {

        // if completionDate != null -> set completion date
        Optional.ofNullable(taskDto.getCompletionDate())
                .ifPresent(task::setCompletionDate);

        // if title != null -> set title
        Optional.ofNullable(taskDto.getTitle())
                .ifPresent(task::setTitle);
    }

    <T> boolean isUserValidOrHasRoleAdmin(User user, T entity) {
        return (entity != null && entity.equals(user)) ||
                user.getRoles().contains(Role.ADMIN);
    }

}

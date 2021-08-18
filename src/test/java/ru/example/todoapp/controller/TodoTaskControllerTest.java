package ru.example.todoapp.controller;
/*
 * Date: 3/14/21
 * Time: 7:17 AM
 * */

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todoapp.controller.request.TodoTaskRequest;
import ru.example.todoapp.dto.TodoTaskDto;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.enums.filters.FilterByDate;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.service.TodoTaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TodoTaskControllerTest extends AbstractControllerTestClass {

    @MockBean
    private TodoTaskService taskService;

    private static final String API_TASKS = "/api/tasks/";

    @Test
    @WithUserDetails(ADMIN)
    public void getTasks_ShouldReturnListOfTasks() throws Exception {
        given(taskService.findTasks(anyInt(), anyInt(),
                any(FilterByDate.class), anyString())).willReturn(List.of(mock(TodoTask.class)));
        given(taskService.mapToTaskDto(any()))
                .willReturn(new TodoTaskDto(1L, "task", LocalDate.now(),
                        false, false,
                        LocalDateTime.now(), LocalDateTime.now()));

        mvc.perform(get(API_TASKS)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.tasks[0].title", is("task")));

        verify(taskService, times(1)).findTasks(anyInt(), anyInt(),
                any(FilterByDate.class), anyString());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void getTask_ShouldReturnTaskById() throws Exception {
        given(taskService.findTaskById(anyLong())).willReturn(mock(TodoTask.class));
        given(taskService.mapToTaskDto(any()))
                .willReturn(new TodoTaskDto(1L, "task", LocalDate.now(),
                        false, false,
                        LocalDateTime.now(), LocalDateTime.now()));

        mvc.perform(get(API_TASKS + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", containsStringIgnoringCase("task")));

        verify(taskService, times(1)).findTaskById(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void getTask_ShouldReturnNotFound() throws Exception {
        given(taskService.findTaskById(anyLong()))
                .willThrow(CustomException.notFound("Task not found"));

        mvc.perform(get(API_TASKS + 1))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));

        verify(taskService, times(1)).findTaskById(anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void deleteTask_ShouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTaskById(anyLong());
        mvc.perform(delete(API_TASKS + 1)).andExpect(status().isNoContent());
        verify(taskService, times(1)).deleteTaskById(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteTask_ShouldReturnNotFound() throws Exception {
        doThrow(CustomException.notFound("Task not found"))
                .when(taskService).deleteTaskById(anyLong());

        mvc.perform(delete(API_TASKS + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));

        verify(taskService, times(1)).deleteTaskById(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteTask_ShouldReturnForbidden() throws Exception {
        doThrow(CustomException.forbidden("Not enough permissions"))
                .when(taskService).deleteTaskById(anyLong());

        mvc.perform(delete(API_TASKS + 1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error", containsStringIgnoringCase("forbidden")));

        verify(taskService, times(1)).deleteTaskById(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_ShouldReturnCreated() throws Exception {
        TodoTask task = mock(TodoTask.class);
        given(task.getTitle()).willReturn("title");

        given(taskService.createTask(any(TodoTaskRequest.class))).willReturn(task);

        String body = "{\"title\": \"Task\"}";
        mvc.perform(post(API_TASKS)
                .contentType(APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        verify(taskService, times(1)).createTask(any(TodoTaskRequest.class));
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_TASKS)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(taskService, times(0)).createTask(any(TodoTaskRequest.class));
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_InvalidTitle_ShouldReturnBadRequest() throws Exception {
        TodoTaskRequest request = new TodoTaskRequest("T", LocalDate.now(), false);
        mvc.perform(post(API_TASKS)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))) // min=3
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("title", containsInAnyOrder("Size must be between 3 and 80")))
                .andDo(print());

        verify(taskService, times(0)).createTask(any(TodoTaskRequest.class));
    }

    @Test
    @WithUserDetails(USER)
    public void updateTask_ShouldReturnBadRequest() throws Exception {
        mvc.perform(patch(API_TASKS + 1)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}

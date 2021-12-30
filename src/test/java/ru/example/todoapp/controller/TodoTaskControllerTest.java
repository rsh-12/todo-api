package ru.example.todoapp.controller;
/*
 * Date: 3/14/21
 * Time: 7:17 AM
 * */

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todoapp.domain.request.TodoTaskRequest;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.service.TodoTaskService;
import ru.example.todoapp.service.dto.TodoTaskDto;
import ru.example.todoapp.service.mapper.TaskMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TodoTaskControllerTest extends AbstractControllerTestClass {

    @MockBean
    private TodoTaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    private static final String API_TASKS = "/api/tasks";

    @Test
    @WithUserDetails(ADMIN)
    public void getTasks_ShouldReturnListOfTasks() throws Exception {
        List<TodoTask> tasks = List.of(new TodoTask("task1"));
        Page<TodoTask> page = new PageImpl<>(tasks);

        TodoTaskDto taskDto = new TodoTaskDto(1L, "task1", LocalDate.now(),
                false, false,
                LocalDateTime.now(), LocalDateTime.now());

        given(taskService.findAll(any(), any())).willReturn(page);
        given(taskMapper.mapToTaskDto(any())).willReturn(taskDto);

        mvc.perform(get(API_TASKS)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.tasks[0].title", is("task1")));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void getTask_ShouldReturnTaskById() throws Exception {
        TodoTask todoTask = mock(TodoTask.class);
        given(taskService.findOne(anyLong())).willReturn(Optional.of(todoTask));
        given(taskMapper.mapToTaskDto(any()))
                .willReturn(new TodoTaskDto(1L, "task", LocalDate.now(),
                        false, false,
                        LocalDateTime.now(), LocalDateTime.now()));

        mvc.perform(get(API_TASKS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", containsStringIgnoringCase("task")));
    }

    @Test
    @WithUserDetails(USER)
    public void getTask_ShouldReturnNotFound() throws Exception {
        given(taskService.findOne(anyLong()))
                .willThrow(CustomException.createNotFoundExc("Task not found"));

        mvc.perform(get(API_TASKS + "/1"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));

        verify(taskService, times(1)).findOne(anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void deleteTask_ShouldReturnNoContent() throws Exception {
        doNothing().when(taskService).delete(anyLong());
        mvc.perform(delete(API_TASKS + "/1")).andExpect(status().isNoContent());
        verify(taskService, times(1)).delete(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteTask_ShouldReturnNotFound() throws Exception {
        doThrow(CustomException.createNotFoundExc("Task not found"))
                .when(taskService).delete(anyLong());

        mvc.perform(delete(API_TASKS + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));

        verify(taskService, times(1)).delete(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteTask_ShouldReturnForbidden() throws Exception {
        doThrow(CustomException.createForbiddenExc("Not enough permissions"))
                .when(taskService).delete(anyLong());

        mvc.perform(delete(API_TASKS + "/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error", containsStringIgnoringCase("forbidden")));

        verify(taskService, times(1)).delete(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_ShouldReturnCreated() throws Exception {
        TodoTask task = mock(TodoTask.class);
        given(task.getTitle()).willReturn("title");

        given(taskService.create(any(TodoTaskRequest.class))).willReturn(task);

        String body = "{\"title\": \"Task\"}";
        mvc.perform(post(API_TASKS)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(status().isCreated());

        verify(taskService).create(any(TodoTaskRequest.class));
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_TASKS)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
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

        verifyNoInteractions(taskService);
    }

    @Test
    @WithUserDetails(USER)
    public void updateTask_ShouldReturnBadRequest() throws Exception {
        mvc.perform(patch(API_TASKS + "/1")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    @WithUserDetails(USER)
    public void updateTask_ShouldReturnOk() throws Exception {
        TodoTask task = mock(TodoTask.class);
        given(task.getId()).willReturn(1L);

        given(taskService.update(anyLong(), any(TodoTaskRequest.class)))
                .willReturn(Optional.of(task));

        TodoTaskRequest request = new TodoTaskRequest("Make a call", LocalDate.now(), true);
        mvc.perform(patch(API_TASKS + "/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(status().isOk());
    }

}

package ru.example.todo.controller;
/*
 * Date: 3/14/21
 * Time: 7:17 AM
 * */

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.dto.TodoTaskDto;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByDate;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.TodoTaskService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TodoTaskControllerTest extends AbstractControllerTestClass {

    @MockBean
    private TodoTaskService taskService;

    @Test
    @WithUserDetails(ADMIN)
    public void getTasks_ShouldReturnListOfTasks() throws Exception {
        given(taskService.findTasks(
                Mockito.any(User.class), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any(FilterByDate.class), Mockito.anyString()))
                .willReturn(List.of(
                        new TodoTask("task1", LocalDate.now()),
                        new TodoTask("task2", LocalDate.now())));

        mvc.perform(get(API_TASKS)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("_embedded.tasks[0].title", is("task1")))
                .andExpect(jsonPath("_embedded.tasks[1].title", is("task2")))
                .andExpect(status().isOk());

        verify(taskService, times(1)).findTasks(
                Mockito.any(User.class), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any(FilterByDate.class), Mockito.anyString());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void getTask_ShouldReturnTaskById() throws Exception {
        given(taskService.findTaskById(Mockito.any(User.class), Mockito.anyLong()))
                .willReturn(new TodoTask("task"));

        mvc.perform(get(API_TASKS + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", containsStringIgnoringCase("task")));

        verify(taskService, times(1)).findTaskById(Mockito.any(User.class), Mockito.anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void getTask_ShouldReturnNotFound() throws Exception {
        given(taskService.findTaskById(Mockito.any(User.class), Mockito.anyLong()))
                .willThrow(new CustomException("Not Found", "Task not found", HttpStatus.NOT_FOUND));

        mvc.perform(get(API_TASKS + 1))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));

        verify(taskService, times(1)).findTaskById(Mockito.any(User.class), Mockito.anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void deleteTask_ShouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTaskById(Mockito.any(User.class), Mockito.anyLong());
        mvc.perform(delete(API_TASKS + 1)).andExpect(status().isNoContent());
        verify(taskService, times(1)).deleteTaskById(Mockito.any(User.class), Mockito.anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteTask_ShouldReturnNotFound() throws Exception {
        doThrow(new CustomException("Not Found", "Task not found", HttpStatus.NOT_FOUND))
                .when(taskService).deleteTaskById(Mockito.any(User.class), Mockito.anyLong());

        mvc.perform(delete(API_TASKS + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));

        verify(taskService, times(1)).deleteTaskById(Mockito.any(User.class), Mockito.anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteTask_ShouldReturnForbidden() throws Exception {
        doThrow(new CustomException("Forbidden", "Not enough permissions", HttpStatus.FORBIDDEN))
                .when(taskService).deleteTaskById(Mockito.any(User.class), Mockito.anyLong());

        mvc.perform(delete(API_TASKS + 1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error", containsStringIgnoringCase("forbidden")));

        verify(taskService, times(1)).deleteTaskById(Mockito.any(User.class), Mockito.anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_ShouldReturnCreated() throws Exception {
        doNothing().when(taskService).createTask(Mockito.any(User.class), Mockito.any(TodoTask.class));

        mvc.perform(post(API_TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(new TodoSectionDto("title"))))
                .andExpect(status().isCreated());

        verify(taskService, times(1)).createTask(Mockito.any(User.class), Mockito.any(TodoTask.class));
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_TASKS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(taskService, times(0)).createTask(Mockito.any(User.class), Mockito.any(TodoTask.class));
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_InvalidTitle_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(new TodoTaskDto("T")))) // min=3
                //.andExpect(jsonPath("title", containsInAnyOrder("Size must be between 3 and 80")))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(taskService, times(0)).createTask(Mockito.any(User.class), Mockito.any(TodoTask.class));
    }

    @Test
    @WithUserDetails(USER)
    public void updateTask_ShouldReturnOk() throws Exception {
        doNothing().when(taskService).updateTask(
                Mockito.any(User.class), Mockito.anyLong(),
                Mockito.any(TodoTaskDto.class),
                Mockito.any(), Mockito.any());

        mvc.perform(patch(API_TASKS + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("starred", "true")
                .param("completed", "true")
                .content(convertToJson("{\"title\":\"some title\"")))
                .andExpect(status().isOk())
                .andDo(print());
    }

}

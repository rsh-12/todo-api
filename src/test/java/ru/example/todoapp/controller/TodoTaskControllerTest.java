package ru.example.todoapp.controller;
/*
 * Date: 3/14/21
 * Time: 7:17 AM
 * */

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todoapp.dto.TodoSectionDto;
import ru.example.todoapp.dto.TodoTaskDto;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.enums.filters.FilterByDate;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.service.TodoTaskService;

import java.time.LocalDate;
import java.util.List;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TodoTaskControllerTest extends AbstractControllerTestClass {

    @MockBean
    private TodoTaskService taskService;

    @Test
    @WithUserDetails(ADMIN)
    public void getTasks_ShouldReturnListOfTasks() throws Exception {
        given(taskService.findTasks(anyInt(), anyInt(),
                any(FilterByDate.class), anyString())).willReturn(List.of(
                new TodoTask("task1", LocalDate.now()),
                new TodoTask("task2", LocalDate.now())));

        mvc.perform(get(API_TASKS)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("_embedded.tasks[0].title", is("task1")))
                .andExpect(jsonPath("_embedded.tasks[1].title", is("task2")))
                .andExpect(status().isOk());

        verify(taskService, times(1)).findTasks(anyInt(), anyInt(),
                any(FilterByDate.class), anyString());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void getTask_ShouldReturnTaskById() throws Exception {
        given(taskService.findTaskById(anyLong())).willReturn(new TodoTask("task"));

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

        given(taskService.createTask(any(TodoTask.class))).willReturn(task);

        mvc.perform(post(API_TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoSectionDto("title"))))
                .andExpect(status().isCreated());

        verify(taskService, times(1)).createTask(any(TodoTask.class));
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_TASKS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(taskService, times(0)).createTask(any(TodoTask.class));
    }

    @Test
    @WithUserDetails(USER)
    public void createTask_InvalidTitle_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoTaskDto("T")))) // min=3
                //.andExpect(jsonPath("title", containsInAnyOrder("Size must be between 3 and 80")))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(taskService, times(0)).createTask(any(TodoTask.class));
    }


}
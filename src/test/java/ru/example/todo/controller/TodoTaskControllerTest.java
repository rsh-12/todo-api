package ru.example.todo.controller;
/*
 * Date: 3/14/21
 * Time: 7:17 AM
 * */

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByBoolean;
import ru.example.todo.enums.filters.FilterByDate;
import ru.example.todo.service.TodoTaskService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Ignore
    @Test
    @WithUserDetails(USER)
    public void getTask_ShouldReturnNotFound() throws Exception {
        final int TASK_ID = 100;

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));
    }

    @Ignore
    @Test
    @WithUserDetails(ADMIN)
    public void deleteTask_ShouldReturnNoContent() throws Exception {
        final int TASK_ID = 6;

        int beforeTasksQuantity = getJsonArraySize(API_TASKS, "_embedded.tasks");

        mvc.perform(delete(API_TASKS + TASK_ID))
                .andExpect(status().isNoContent());

        int afterTasksQuantity = getJsonArraySize(API_TASKS, "_embedded.tasks");

        assertEquals(beforeTasksQuantity - 1, afterTasksQuantity);
    }

    @Ignore
    @Test
    @WithUserDetails(USER)
    public void deleteTask_ShouldReturnNotFound() throws Exception {
        final int TASK_ID = 100;

        int beforeTasksQuantity = getJsonArraySize(API_TASKS, "_embedded.tasks");

        mvc.perform(delete(API_TASKS + TASK_ID))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));

        int afterTasksQuantity = getJsonArraySize(API_TASKS, "_embedded.tasks");

        assertEquals(beforeTasksQuantity, afterTasksQuantity);
    }

    @Ignore
    @Test
    @WithUserDetails(USER)
    public void createTask_ShouldReturnStatusCreated() throws Exception {
        int beforeTasksQuantity = getJsonArraySize(API_TASKS, "_embedded.tasks");

        final String body = String.format("{\"title\": \"%s\", \"completionDate\": \"%s\"}", "New Title", "2022-12-12");

        mvc.perform(post(API_TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        int afterTasksQuantity = getJsonArraySize(API_TASKS, "_embedded.tasks");

        assertEquals(beforeTasksQuantity + 1, afterTasksQuantity);

        mvc.perform(get(API_TASKS + 11))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Ignore
    @Test
    @WithUserDetails(USER)
    public void updateTask_ShouldReturnNotFound() throws Exception {
        final int TASK_ID = 100;

        mvc.perform(patch(API_TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));
    }

    @Ignore
    @Test
    @WithUserDetails(USER)
    public void updateTask_Title_ShouldReturnStatusOk() throws Exception {
        final int TASK_ID = 2;
        final String body = String.format("{\"title\": \"%s\"}", "New title");

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", containsStringIgnoringCase("Create a presentation")));

        mvc.perform(patch(API_TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", containsStringIgnoringCase("New title")));
    }

    @Ignore
    @Test
    @WithUserDetails(USER)
    public void updateTask_CompletionDate_ShouldReturnStatusOk() throws Exception {
        final int TASK_ID = 5;
        final String newCompletionDate = "2022-12-12";
        final String body = String.format("{\"completionDate\": \"%s\"}", newCompletionDate);

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("completionDate", containsStringIgnoringCase(LocalDate.now().toString())));

        mvc.perform(patch(API_TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("completionDate", containsStringIgnoringCase(newCompletionDate)));

    }

    @Ignore
    @Test
    @WithUserDetails(USER)
    public void updateTask_Completed_ShouldReturnStatusOk() throws Exception {
        final int TASK_ID = 2;

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("completed", is(false)));

        mvc.perform(patch(API_TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("completed", FilterByBoolean.TRUE.name()))
                .andExpect(status().isOk());

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("completed", is(true)));
    }

    @Ignore
    @Test
    @WithUserDetails(ADMIN)
    public void updateTask_Starred_ShouldReturnStatusOk() throws Exception {
        final int TASK_ID = 3;

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("starred", is(false)));

        mvc.perform(patch(API_TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("starred", FilterByBoolean.TRUE.name()))
                .andExpect(status().isOk());

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("starred", is(true)));
    }

    @Ignore
    @Test
    @WithUserDetails(ADMIN)
    public void updateTask_TitleCompletedStarredCompletionDate_ShouldReturnStatusOk() throws Exception {
        final int TASK_ID = 4;
        final String newTitle = "New title";
        final String newCompletionDate = "2022-12-12";

        final String body = String.format("{\"title\": \"%s\",\"completionDate\": \"%s\"}",
                newTitle, newCompletionDate);

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("title", containsStringIgnoringCase("Section task")))
                .andExpect(jsonPath("completed", is(false)))
                .andExpect(jsonPath("starred", is(false)))
                .andExpect(jsonPath("completionDate", containsStringIgnoringCase(LocalDate.now().toString())));

        mvc.perform(patch(API_TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .param("completed", FilterByBoolean.TRUE.name())
                .param("starred", FilterByBoolean.TRUE.name()));

        mvc.perform(get(API_TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("title", containsStringIgnoringCase(newTitle)))
                .andExpect(jsonPath("completed", is(true)))
                .andExpect(jsonPath("starred", is(true)))
                .andExpect(jsonPath("completionDate", containsStringIgnoringCase(newCompletionDate)));
    }

    @Ignore
    @Test
    @WithUserDetails(USER)
    public void deleteTask_ShouldReturnForbidden() throws Exception {
        mvc.perform(delete(API_TASKS + 4))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("not enough permissions")))
                .andDo(print());
    }
}

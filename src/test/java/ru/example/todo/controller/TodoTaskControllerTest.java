package ru.example.todo.controller;
/*
 * Date: 3/14/21
 * Time: 7:17 AM
 * */

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import ru.example.todo.enums.TaskDate;
import ru.example.todo.enums.TaskStatus;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo update tests
public class TodoTaskControllerTest extends AbstractControllerTestClass {

    @Test
    @WithUserDetails(ADMIN)
    public void testGetAllTasks_WithAndWithoutParams() throws Exception {
        mvc.perform(get(TASKS)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get(TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .param("date", TaskDate.TODAY.name()))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get(TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .param("date", TaskDate.OVERDUE.name()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testGetTodaysTasks() throws Exception {
        int size = getJsonArraySize(TASKS, "_embedded.tasks", "date", TaskDate.TODAY.name());
        assertTrue(size > 0);

        ResultActions actions = mvc.perform(get(TASKS).param("date", TaskDate.TODAY.name())).andDo(print());

        String today = LocalDate.now().toString();
        for (int i = 0; i < size; i++) {
            actions.andExpect(jsonPath(String.format("_embedded.tasks[%d].completionDate", i), is(today)));
        }
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testGetTaskById() throws Exception {
        final int TASK_ID = 3;

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title",
                        containsStringIgnoringCase("Write a letter")));
    }

    @Test
    @WithUserDetails(USER)
    public void testGetTaskById_NotFound() throws Exception {
        final int TASK_ID = 100;

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testDeleteTaskById() throws Exception {
        final int TASK_ID = 6;

        int beforeTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        mvc.perform(delete(TASKS + TASK_ID))
                .andExpect(status().isNoContent());

        int afterTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        assertEquals(beforeTasksQuantity - 1, afterTasksQuantity);
    }

    @Test
    @WithUserDetails(USER)
    public void testDeleteTaskById_NotFound() throws Exception {
        final int TASK_ID = 100;

        int beforeTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        mvc.perform(delete(TASKS + TASK_ID))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));

        int afterTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        assertEquals(beforeTasksQuantity, afterTasksQuantity);
    }

    @Test
    @WithUserDetails(USER)
    public void testCreateNewTask() throws Exception {
        int beforeTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        final String body = String.format("{\"title\": \"%s\", \"completionDate\": \"%s\"}", "New Title", "2022-12-12");

        mvc.perform(post(TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        int afterTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        assertEquals(beforeTasksQuantity + 1, afterTasksQuantity);

        mvc.perform(get(TASKS + 11))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithUserDetails(USER)
    public void testUpdateTask_NotFound() throws Exception {
        final int TASK_ID = 100;

        mvc.perform(patch(TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Task not found")));
    }

    @Test
    @WithUserDetails(USER)
    public void testUpdateTask_Title() throws Exception {
        final int TASK_ID = 2;
        final String body = String.format("{\"title\": \"%s\"}", "New title");

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", containsStringIgnoringCase("Create a presentation")));

        mvc.perform(patch(TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", containsStringIgnoringCase("New title")));
    }

    @Test
    @WithUserDetails(USER)
    public void testUpdateTask_CompletionDate() throws Exception {
        final int TASK_ID = 5;
        final String newCompletionDate = "2022-12-12";
        final String body = String.format("{\"completionDate\": \"%s\"}", newCompletionDate);

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("completionDate", containsStringIgnoringCase(LocalDate.now().toString())));

        mvc.perform(patch(TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("completionDate", containsStringIgnoringCase(newCompletionDate)));

    }

    @Test
    @WithUserDetails(USER)
    public void testUpdateTask_Completed() throws Exception {
        final int TASK_ID = 2;

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("completed", is(false)));

        mvc.perform(patch(TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("completed", TaskStatus.TRUE.name()))
                .andExpect(status().isOk());

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("completed", is(true)));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testUpdateTask_Starred() throws Exception {
        final int TASK_ID = 3;

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("starred", is(false)));

        mvc.perform(patch(TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("starred", TaskStatus.TRUE.name()))
                .andExpect(status().isOk());

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("starred", is(true)));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testUpdateTask_AllInOne() throws Exception {
        final int TASK_ID = 4;
        final String newTitle = "New title";
        final String newCompletionDate = "2022-12-12";

        final String body = String.format("{\"title\": \"%s\",\"completionDate\": \"%s\"}",
                newTitle, newCompletionDate);

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("title", containsStringIgnoringCase("Section task")))
                .andExpect(jsonPath("completed", is(false)))
                .andExpect(jsonPath("starred", is(false)))
                .andExpect(jsonPath("completionDate", containsStringIgnoringCase(LocalDate.now().toString())));

        mvc.perform(patch(TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .param("completed", TaskStatus.TRUE.name())
                .param("starred", TaskStatus.TRUE.name()));

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("title", containsStringIgnoringCase(newTitle)))
                .andExpect(jsonPath("completed", is(true)))
                .andExpect(jsonPath("starred", is(true)))
                .andExpect(jsonPath("completionDate", containsStringIgnoringCase(newCompletionDate)));
    }

    @Test
    @WithUserDetails(USER)
    public void deleteTaskById_ShouldThrowCustomException() throws Exception {
        mvc.perform(delete(TASKS + 4))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("not enough permissions")))
                .andDo(print());
    }
}

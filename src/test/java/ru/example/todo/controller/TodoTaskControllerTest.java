package ru.example.todo.controller;
/*
 * Date: 3/14/21
 * Time: 7:17 AM
 * */

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import ru.example.todo.enums.TaskStatus;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Test data:
 * Read a book, Create a presentation, Write a letter
 * */
public class TodoTaskControllerTest extends AbstractTestContollerClass {

    @Test
    public void testGetAllTasks_WithAndWithoutParams() throws Exception {
        /*
         * param "day" == completionDate
         * */
        mvc.perform(get(TASKS)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get(TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .param("date", "today"))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get(TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .param("date", "overdue"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTodaysTasks() throws Exception {
        int size = getJsonArraySize(TASKS, "_embedded.tasks", "date", "today");
        assertTrue(size > 0);

        ResultActions actions = mvc.perform(get(TASKS).param("date", "today")).andDo(print());

        String today = LocalDate.now().toString();
        for (int i = 0; i < size; i++) {
            actions.andExpect(jsonPath(String.format("_embedded.tasks[%d].completionDate", i), is(today)));
        }
    }

    @Test
    public void testGetTaskById() throws Exception {
        final int TASK_ID = 1;

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(TASK_ID)));
    }

    @Test
    public void testGetTaskById_NotFound() throws Exception {
        final int TASK_ID = 100;

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("message", containsString("Task not found")));
    }

    @Test
    public void testDeleteTaskById() throws Exception {
        final int TASK_ID = 2;

        int beforeTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        mvc.perform(delete(TASKS + TASK_ID))
                .andExpect(status().isNoContent());

        int afterTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        assertEquals(beforeTasksQuantity - 1, afterTasksQuantity);
    }

    @Test
    public void testDeleteTaskById_NotFound() throws Exception {
        final int TASK_ID = 100;

        int beforeTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        mvc.perform(delete(TASKS + TASK_ID))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsString("Task not found")));

        int afterTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        assertEquals(beforeTasksQuantity, afterTasksQuantity);
    }

    @Test
    public void testCreateNewTask() throws Exception {
        int beforeTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        final String body = String.format("{\"title\": \"%s\", \"completionDate\": \"%s\"}", "New Title", "2022-12-12");

        mvc.perform(post(TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        int afterTasksQuantity = getJsonArraySize(TASKS, "_embedded.tasks");

        assertEquals(beforeTasksQuantity + 1, afterTasksQuantity);

        mvc.perform(get(TASKS + afterTasksQuantity))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testUpdateTask_Title() throws Exception {
        final int TASK_ID = 3;
        final String body = String.format("{\"title\": \"%s\"}", "New title");

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", containsString("Write a letter")));

        mvc.perform(patch(TASKS + TASK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", containsString("New title")));
    }


    @Test
    public void testUpdateTask_Completed() throws Exception {
        final int TASK_ID = 3;

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
}

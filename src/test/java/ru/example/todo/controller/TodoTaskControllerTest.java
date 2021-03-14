package ru.example.todo.controller;
/*
 * Date: 3/14/21
 * Time: 7:17 AM
 * */

import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(status().isOk());

        mvc.perform(get(TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .param("date", "today"))
                .andExpect(status().isOk());

        mvc.perform(get(TASKS)
                .contentType(MediaType.APPLICATION_JSON)
                .param("date", "overdue"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTaskById() throws Exception {
        final int TASK_ID = 1;

        mvc.perform(get(TASKS + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(TASK_ID)));
    }
}

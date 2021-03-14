package ru.example.todo.controller;
/*
 * Date: 3/12/21
 * Time: 10:30 PM
 * */


import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ru.example.todo.controller.wrapper.TaskIdsWrapper;
import ru.example.todo.entity.TodoSection;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Test data:
 * Important, Starred, Later
 * */

public class TodoSectionControllerTest extends AbstractTestContollerClass {

    // get all sections
    @Test
    public void A_testGetAllTodoSections() throws Exception {
        mvc.perform(get(SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections[0].title", is("Important")))
                .andDo(print());
    }

    // get section by ID
    @Test
    public void B_testGetTodoSectionById() throws Exception {
        final int SECTION_ID = 1;

        mvc.perform(get(SECTIONS + SECTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Important")))
                .andDo(print());
    }

    // create new section
    @Test
    public void C_testCreateNewSection() throws Exception {

        TodoSection section = new TodoSection();
        section.setTitle("CreatedSection");

        MvcResult beforeResult = mvc.perform(get(SECTIONS)).andExpect(status().isOk()).andReturn();

        int beforeQuantity = JsonPath.read(beforeResult.getResponse().getContentAsString(),
                "_embedded.sections.length()");

        mvc.perform(post(SECTIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(section)))
                .andExpect(status().isCreated())
                .andDo(print());

        MvcResult afterResult = mvc.perform(get(SECTIONS)).andExpect(status().isOk()).andReturn();

        int afterQuantity = JsonPath.read(afterResult.getResponse().getContentAsString(),
                "_embedded.sections.length()");

        assertEquals(beforeQuantity + 1, afterQuantity);
    }


    // delete section
    @Test
    public void D_testDeleteSectionById() throws Exception {
        final int SECTION_ID = 1;

        // get by id: returns 200 OK
        mvc.perform(get(SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // delete by id: returns 204 NO CONTENT
        mvc.perform(delete(SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        // get again by id: returns 404 NOT FOUND
        mvc.perform(get(SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // update section
    @Test
    public void E_testUpdateSectionById() throws Exception {
        final int SECTION_ID = 2;

        // get section by id: returns 200 OK
        mvc.perform(get(SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Starred")));

        // update section by id: returns 200 OK
        mvc.perform(put(SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson(2L, "NewTitle")))
                .andExpect(status().isOk())
                .andDo(print());

        // get section by id, check new title: returns 200 OK
        mvc.perform(get(SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("NewTitle")));
    }

    private String getSectionInJson(Long id, String title) {
        return String.format("{\"id\":%d, \"title\":\"%s\"}", id, title);
    }

    // get section by non-existent id: returns 404 NOT FOUND
    @Test
    public void testSectionByIdNotFound() throws Exception {
        final int SECTION_ID = 100;

        mvc.perform(get(SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    // todo: add additional checks
    // delete section by non-existent id: returns 204 NO CONTENT
    @Test
    public void testDeleteSectionByNoneExistentId() throws Exception {
        final int SECTION_ID = 100;

        mvc.perform(delete(SECTIONS + SECTION_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateSectionByNoneExistentId() throws Exception {
        final int SECTION_ID = 100;

        mvc.perform(put(SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson((long) SECTION_ID, "New title")))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsString("Section not found")));
    }

    @Test
    public void testUpdateSectionById_InvalidData() throws Exception {
        final int SECTION_ID = 3;

        mvc.perform(put(SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson((long) SECTION_ID, "T")))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("title", containsInAnyOrder("Size must be between 3 and 50")));
    }

    // add tasks to the section
    @Test
    public void testAddTasks() throws Exception {

        final int SECTION_ID = 3;

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>() {{
            addAll(Set.of(4L, 5L, 6L));
        }};

        mvc.perform(get(SECTIONS + SECTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("tasks", hasSize(1))); // already exists 1 task

        mvc.perform(post(SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "move")
                .content(asJsonString(wrapper)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(status().isOk());

        mvc.perform(get(SECTIONS + SECTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("tasks", hasSize(1 + wrapper.tasks.size()))); // 1 + new 3
    }

    // remove task(s) from the section

    @Test
    public void testRemoveTaskFromSection() throws Exception {

        final int TASK_ID = 2, SECTION_ID = 2;

        mvc.perform(get(SECTIONS + SECTION_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("tasks", hasSize(1))) // already exists 1 task
                .andExpect(jsonPath("tasks[0].id", is(TASK_ID))); // already exists 1 task

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>() {{
            add((long) TASK_ID);
        }};
    }
}

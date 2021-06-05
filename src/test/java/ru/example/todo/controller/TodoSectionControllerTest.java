package ru.example.todo.controller;
/*
 * Date: 3/12/21
 * Time: 10:30 PM
 * */


import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todo.controller.wrapper.TaskIdsWrapper;
import ru.example.todo.dto.TodoSectionDto;

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
// todo update tests
public class TodoSectionControllerTest extends AbstractControllerTestClass {

    // get all sections
    @Test
    @WithUserDetails(ADMIN)
    public void A_testGetAllTodoSections() throws Exception {
        mvc.perform(get(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections[0].title", is("Important")))
                .andDo(print());
    }

    // get section by ID
    @Test
    @WithUserDetails(ADMIN)
    public void B_testGetTodoSectionById() throws Exception {
        final int SECTION_ID = 1;

        mvc.perform(get(API_SECTIONS + SECTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Important")))
                .andDo(print());
    }

    // create new section
    @Test
    @WithUserDetails(ADMIN)
    public void C_testCreateNewSection() throws Exception {

        TodoSectionDto section = new TodoSectionDto();
        section.setTitle("Created Section");

        int beforeSectionsQuantity = getJsonArraySize(API_SECTIONS, "_embedded.sections");

        mvc.perform(post(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(section)))
                .andExpect(status().isCreated())
                .andDo(print());

        mvc.perform(get(API_SECTIONS + 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Created Section")))
                .andDo(print());

        int afterSectionsQuantity = getJsonArraySize(API_SECTIONS, "_embedded.sections");

        assertEquals(beforeSectionsQuantity + 1, afterSectionsQuantity);
    }


    // delete section
    @Test
    @WithUserDetails(ADMIN)
    public void D_testDeleteSectionById() throws Exception {
        final int SECTION_ID = 1;

        // get by id: returns 200 OK
        mvc.perform(get(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // delete by id: returns 204 NO CONTENT
        mvc.perform(delete(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        // get again by id: returns 404 NOT FOUND
        mvc.perform(get(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // update section
    @Test
    @WithUserDetails(ADMIN)
    public void E_testUpdateSectionById() throws Exception {
        final int SECTION_ID = 3;

        // get section by id: returns 200 OK
        mvc.perform(get(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Later")));

        // update section by id: returns 200 OK
        mvc.perform(put(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson(2L, "NewTitle")))
                .andExpect(status().isOk())
                .andDo(print());

        // get section by id, check new title: returns 200 OK
        mvc.perform(get(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("NewTitle")));
    }

    private String getSectionInJson(Long id, String title) {
        return String.format("{\"id\":%d, \"title\":\"%s\"}", id, title);
    }

    // get section by non-existent id: returns 404 NOT FOUND
    @Test
    @WithUserDetails(USER)
    public void testSectionById_NotFound() throws Exception {
        final int SECTION_ID = 100;

        mvc.perform(get(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    // delete section by non-existent id: returns 204 NO CONTENT
    @Test
    @WithUserDetails(USER)
    public void testDeleteSectionByNoneExistentId() throws Exception {
        final int SECTION_ID = 100;

        int beforeSectionsQuantity = getJsonArraySize(API_SECTIONS, "_embedded.sections");

        mvc.perform(delete(API_SECTIONS + SECTION_ID))
                .andExpect(status().isNotFound());

        int afterSectionsQuantity = getJsonArraySize(API_SECTIONS, "_embedded.sections");

        assertEquals(beforeSectionsQuantity, afterSectionsQuantity);
    }


    @Test
    @WithUserDetails(USER)
    public void testUpdateSectionByNoneExistentId() throws Exception {
        final int SECTION_ID = 100;

        mvc.perform(put(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson((long) SECTION_ID, "New title")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Section not found")));
    }

    @Test
    public void testUpdateSectionById_InvalidData() throws Exception {
        final int SECTION_ID = 3;

        mvc.perform(put(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson((long) SECTION_ID, "T")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("title", containsInAnyOrder("Size must be between 3 and 50")));
    }

    // add tasks to the section
    @Test
    @WithUserDetails(ADMIN)
    public void testAddTasks() throws Exception {
        final int SECTION_ID = 3;

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>() {{
            addAll(Set.of(4L, 6L));
        }};

        int beforeTasksQuantity = getJsonArraySize(API_SECTIONS + SECTION_ID, "tasks");

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "move")
                .content(convertToJson(wrapper)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(status().isOk());

        int afterTasksQuantity = getJsonArraySize(API_SECTIONS + SECTION_ID, "tasks");

        assertEquals(beforeTasksQuantity + wrapper.tasks.size(), afterTasksQuantity);
    }

    // remove task(s) from the section
    @Test
    @WithUserDetails(ADMIN)
    public void testRemoveTaskFromSection() throws Exception {

        final int TASK_ID = 10, SECTION_ID = 4;

        int beforeTasksQuantity = getJsonArraySize(API_SECTIONS + SECTION_ID, "tasks");
        System.out.println("beforeTasksQuantity = " + beforeTasksQuantity);

        assertEquals(1, beforeTasksQuantity);

        mvc.perform(get(API_SECTIONS + SECTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("tasks[0].title", containsStringIgnoringCase("special")));

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>() {{
            add((long) TASK_ID);
        }};

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "remove")
                .content(convertToJson(wrapper)))
                .andExpect(status().isOk());

        int afterTasksQuantity = getJsonArraySize(API_SECTIONS + SECTION_ID, "tasks");

        assertEquals(beforeTasksQuantity - wrapper.tasks.size(), afterTasksQuantity);
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testAddTasksEmptySet() throws Exception {
        final int SECTION_ID = 3;

        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>();

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "move")
                .content(convertToJson(wrapper)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Tasks IDs are required")));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testAddTasks_Null() throws Exception {
        final int SECTION_ID = 3;

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = null;

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "remove")
                .content(convertToJson(wrapper)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Tasks IDs are required")));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void testAddTasks_SectionNotFound() throws Exception {
        final int SECTION_ID = 100;

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>() {{
            add(4L);
        }};

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "remove")
                .content(convertToJson(wrapper)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Section not found")));
    }
}

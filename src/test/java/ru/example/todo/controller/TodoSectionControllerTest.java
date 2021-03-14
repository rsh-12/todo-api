package ru.example.todo.controller;
/*
 * Date: 3/12/21
 * Time: 10:30 PM
 * */


import org.junit.Test;
import org.springframework.http.MediaType;
import ru.example.todo.entity.TodoSection;

import static org.hamcrest.Matchers.is;
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

        mvc.perform(get(SECTIONS + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Important")))
                .andDo(print());
    }

    // create new section
    @Test
    public void C_testCreateNewSection() throws Exception {

        TodoSection section = new TodoSection();
        section.setTitle("CreatedSection");

        mvc.perform(post(SECTIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(section)))
                .andExpect(status().isCreated())
                .andDo(print());
    }


    // delete section
    @Test
    public void D_testDeleteSectionById() throws Exception {

        // get by id: returns 200 OK
        mvc.perform(get(SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // delete by id: returns 204 NO CONTENT
        mvc.perform(delete(SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        // get again by id: returns 404 NOT FOUND
        mvc.perform(get(SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // update section
    @Test
    public void E_testUpdateSectionById() throws Exception {

        // get section by id: returns 200 OK
        mvc.perform(get(SECTIONS + 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Starred")));

        // update section by id: returns 200 OK
        mvc.perform(put(SECTIONS + 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson(2L)))
                .andExpect(status().isOk())
                .andDo(print());

        // get section by id, check new title: returns 200 OK
        mvc.perform(get(SECTIONS + 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("NewTitle")));
    }

    private String getSectionInJson(Long id) {
        return String.format("{\"id\":%d, \"title\":\"NewTitle\"}", id);
    }

    @Test
    public void testSectionByIdNotFound() throws Exception {
        mvc.perform(get(SECTIONS + 100)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }
}

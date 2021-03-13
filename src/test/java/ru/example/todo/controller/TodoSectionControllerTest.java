package ru.example.todo.controller;
/*
 * Date: 3/12/21
 * Time: 10:30 PM
 * */


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.todo.entity.TodoSection;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TodoSectionControllerTest {

    @Autowired
    private MockMvc mvc;

    private final static String API = "/api/sections/";


    // get all sections
    @Test
    public void testGetAllTodoSections() throws Exception {

        mvc.perform(get(API)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections[0].title", is("Important")))
                .andDo(print());
    }

    // get section by ID
    @Test
    public void testGetTodoSectionById() throws Exception {

        mvc.perform(get(API + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Important")))
                .andDo(print());
    }

    // create new section
    @Test
    public void testCreateNewSection() throws Exception {

        TodoSection section = new TodoSection();
        section.setTitle("CreatedSection");

        mvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(section)))
                .andExpect(status().isCreated())
                .andDo(print());
    }


    // delete section
    @Test
    public void testDeleteSectionById() throws Exception {

        // get by id: returns 200 OK
        mvc.perform(get(API + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // delete by id: returns 204 NO CONTENT
        mvc.perform(delete(API + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        // get again by id: returns 404 NOT FOUND
        mvc.perform(get(API + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    // update section
    @Test
    public void testUpdateSectionById() throws Exception {

        // get section by id: returns 200 OK
        mvc.perform(get(API + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Important")));

        // update section by id: returns 200 OK
        mvc.perform(put(API + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson(1L)))
                .andExpect(status().isOk())
                .andDo(print());

        // get section by id, check new title: returns 200 OK
        mvc.perform(get(API + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("NewTitle")));
    }

    private String getSectionInJson(Long id) {
        return String.format("{\"id\":%d, \"title\":\"NewTitle\"}", id);
    }

    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during converting");
        }
    }
}

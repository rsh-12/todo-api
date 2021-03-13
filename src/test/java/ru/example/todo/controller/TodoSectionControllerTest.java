package ru.example.todo.controller;
/*
 * Date: 3/12/21
 * Time: 10:30 PM
 * */


import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.repository.TodoSectionRepository;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TodoSectionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TodoSectionRepository repository;

    private final static String API = "/api/sections/";

    public void createSection() {
        TodoSection section = new TodoSection("TestSection");
        repository.save(section);
    }

    @Test
    @DisplayName("Test /api/sections/ returns list of sections and 200 OK")
    public void testGetAllTodoSections() throws Exception {

        createSection();

        mvc.perform(get(API)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections[0].title", is("TestSection")))
                .andDo(print())
                .andReturn();
    }

//    @Test
//    public void testGetTodoSectionById() throws Exception {
//
//        mvc.perform(get(API + 1))
//    }

}

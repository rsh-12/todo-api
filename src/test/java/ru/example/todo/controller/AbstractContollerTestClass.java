package ru.example.todo.controller;
/*
 * Date: 3/14/21
 * Time: 7:05 AM
 * */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
abstract class AbstractControllerTestClass {

    protected static final String API_SECTIONS = "/api/sections/";
    protected static final String API_TASKS = "/api/tasks/";
    protected static final String API_USERS = "/api/users/";
    protected static final String API_AUTH = "/api/auth/";

    protected static final String ADMIN = "admin@mail.com";
    protected static final String USER = "client@mail.com";

    @Autowired
    protected MockMvc mvc;

    protected static String convertToJson(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Conversion error");
        }
    }

}

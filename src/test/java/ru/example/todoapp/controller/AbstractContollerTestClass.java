package ru.example.todoapp.controller;
/*
 * Date: 3/14/21
 * Time: 7:05 AM
 * */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
abstract class AbstractControllerTestClass {

    protected static final String API_SECTIONS = "/api/sections/";
    protected static final String API_TASKS = "/api/tasks/";
    protected static final String API_USERS = "/api/users/";

    protected static final String ADMIN = "admin@mail.com";
    protected static final String USER = "client@mail.com";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    String usernamePasswordRequestBody(String username, String password) throws JsonProcessingException {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("password", password);
        return objectMapper.writeValueAsString(body);
    }

}

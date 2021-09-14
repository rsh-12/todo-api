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
import ru.example.todoapp.controller.request.CredentialsRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
abstract class AbstractControllerTestClass {

    protected static final String ADMIN = "admin@mail.com";
    protected static final String USER = "client@mail.com";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String requestOf(String username, String password) {
        CredentialsRequest request = new CredentialsRequest(username, password);
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            return String.format("{\"%s\": \"%s\"}", username, password);
        }
    }

}

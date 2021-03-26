package ru.example.todo.controller;
/*
 * Date: 3/26/21
 * Time: 2:10 PM
 * */

import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends AbstractTestContollerClass {

    @Test
    public void testLogin() throws Exception {

        Map<String, String> body = new LinkedHashMap<>();
        body.put("username", "admin@mail.com");
        body.put("password", "admin");

        String jsonBody = asJsonString(body);

        String response = mvc.perform(post(USERS + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("access_token"));
        assertTrue(response.contains("access_token_exp"));
        assertTrue(response.contains("refresh_token"));
        assertTrue(response.contains("refresh_token_exp"));
    }
}

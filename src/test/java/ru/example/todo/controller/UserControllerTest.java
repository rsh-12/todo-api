package ru.example.todo.controller;
/*
 * Date: 3/26/21
 * Time: 2:10 PM
 * */

import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo update tests
public class UserControllerTest extends AbstractTestContollerClass {

    private String requestBody(String username, String password) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("password", password);
        return asJsonString(body);
    }

    // Login: success
    @Test
    public void testLogin() throws Exception {

        String body = requestBody("admin@mail.com", "admin");

        String response = mvc.perform(post(USERS + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("access_token"));
        assertTrue(response.contains("refresh_token"));
        assertTrue(response.contains("token_type"));
        assertTrue(response.contains("expires"));
    }

    // Login: fail
    @Test
    public void testLogin_NotFound() throws Exception {
        String body = requestBody("usernameNotExists", "client");

        mvc.perform(post(USERS + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsString("Invalid username/password")));
    }

    @Test
    public void testLogin_WrongPassword() throws Exception {
        String body = requestBody(USER, "wrongpassword");

        mvc.perform(post(USERS + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", containsString("Invalid username/password")));
    }

// Register: success
    // Register: fail
    // Token: success
    // Token: fail
    // Delete user: success
    // Delete user:  fail
}

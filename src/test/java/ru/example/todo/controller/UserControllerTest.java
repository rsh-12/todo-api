package ru.example.todo.controller;
/*
 * Date: 3/26/21
 * Time: 2:10 PM
 * */

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo update tests
public class UserControllerTest extends AbstractContollerClass {

    private String requestBody(String username, String password) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("password", password);
        return asJsonString(body);
    }

    // Login: success
    @Test
    public void testLogin() throws Exception {
        String body = requestBody(ADMIN, "admin");

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
        String body = requestBody("usernameNotExists@mail.com", "client");

        mvc.perform(post(USERS + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsString("Username not found or incorrect password")));
    }

    @Test
    public void testLogin_WrongPassword() throws Exception {
        String body = requestBody(USER, "wrongpassword");

        mvc.perform(post(USERS + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", containsString("Username not found or incorrect password")));
    }

    // Register: success
    @Test
    public void testRegister() throws Exception {
        String body = requestBody("newUsername@mail.com", "newPassword");

        MvcResult result = mvc.perform(post(USERS + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("ok", response);
    }

    // Register: fail
    @Test
    public void testRegister_InvalidUsername() throws Exception {
        String body = requestBody("notValidUsername", "password");

        mvc.perform(post(USERS + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("username", containsInAnyOrder("Not a valid email address")));
    }

    @Test
    public void testRegister_InvalidPassword() throws Exception {
        String body = requestBody("username@mail.com", "1");

        mvc.perform(post(USERS + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("password", containsInAnyOrder("Password is required")));
    }

    // Delete user: success
    @Test
    @WithUserDetails(ADMIN)
    public void testDeleteUser() throws Exception {
        final int USER_ID = 4;

        mvc.perform(delete(USERS + USER_ID))
                .andExpect(status().isNoContent());

        mvc.perform(delete(USERS + USER_ID))
                .andExpect(status().isNotFound());
    }

    // Delete user:  fail
    @Test
    @WithUserDetails(USER)
    public void testDeleteUser_Forbidden() throws Exception {
        final int USER_ID = 4;

        mvc.perform(delete(USERS + USER_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", containsString("Not enough permissions")));

    }

    // Token: fail
    @Test
    public void testRefreshTokens_Fail() throws Exception {

        mvc.perform(post(USERS + "token")
                .header("token", "non-existent-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message",
                        containsString("Refresh token is not valid or expired, please, try to log in")));
    }
}

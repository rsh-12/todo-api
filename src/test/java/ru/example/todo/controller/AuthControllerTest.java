package ru.example.todo.controller;
/*
 * Date: 5/15/21
 * Time: 6:25 PM
 * */

import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTestClass {

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

        String response = mvc.perform(post(AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("access_token"));
        assertTrue(response.contains("refresh_token"));
        assertTrue(response.contains("token_type"));
        assertTrue(response.contains("access_token_expires"));
        assertTrue(response.contains("refresh_token_expires"));
    }

    // Login: fail
    @Test
    public void testLogin_NotFound() throws Exception {
        String body = requestBody("usernameNotExists@mail.com", "client");

        mvc.perform(post(AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Username not found / incorrect password")));
    }

    @Test
    public void testLogin_WrongPassword() throws Exception {
        String body = requestBody(USER, "wrongpassword");

        mvc.perform(post(AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Username not found / incorrect password")));
    }

    // Register: success
    @Test
    public void testRegister() throws Exception {
        String body = requestBody("newUsername@mail.com", "newPassword");

        MvcResult result = mvc.perform(post(AUTH + "register")
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

        mvc.perform(post(AUTH + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("username", containsInAnyOrder("Not a valid email address")));
    }

    @Test
    public void testRegister_InvalidPassword() throws Exception {
        String body = requestBody("username@mail.com", "1");

        mvc.perform(post(AUTH + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("password", containsInAnyOrder("Password is required")));
    }


    // Token: fail
    @Test
    public void testRefreshTokens_Fail() throws Exception {

        mvc.perform(post(AUTH + "token")
                .header("token", "non-existent-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("Refresh token is not valid or expired, please, try to log in")));
    }

    @Test
    public void createAndSendOtp_ShouldThrowException() throws Exception {
        mvc.perform(post(AUTH + "/password/forgot"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void createAndSendOtp_ShouldThrowNotFound() throws Exception {

        JSONObject body = new JSONObject();
        body.put("email", "non-existent-email");

        mvc.perform(post(AUTH + "/password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("username not found")))
                .andDo(print());
    }
}

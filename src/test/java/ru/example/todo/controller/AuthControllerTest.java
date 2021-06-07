package ru.example.todo.controller;
/*
 * Date: 5/15/21
 * Time: 6:25 PM
 * */

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.JwtTokenService;
import ru.example.todo.service.UserService;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTestClass {

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private UserService userService;

    private String requestBody(String username, String password) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("password", password);
        return convertToJson(body);
    }

    // Login: success
    @Test
    public void login_ShouldReturnTokens() throws Exception {
        UserDto admin = new UserDto(ADMIN, "admin");

        given(jwtTokenService.buildAccessToken(ADMIN, Collections.singleton(Role.ADMIN)))
                .willReturn("access_token");

        given(jwtTokenService.buildRefreshToken(ADMIN))
                .willReturn(new RefreshToken("refresh_token", ADMIN, new Date()));

        given(userService.login(new UserDto(ADMIN, "admin")))
                .willReturn("access_token");

        assertEquals(userService.login(admin), "access_token");

        String response = mvc.perform(post(API_AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(admin.getUsername(), admin.getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("access_token"));
    }

    // Login: fail
    @Test
    public void login_NotFound_ShouldThrowCustomException() throws Exception {

        UserDto user = new UserDto("usernameNotExists@mail.com", "somePassword");

        given(userService.login(user))
                .willThrow(new CustomException("Not Found",
                        "Username Not Found / Incorrect Password", HttpStatus.NOT_FOUND));

        mvc.perform(post(API_AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(user.getUsername(), user.getPassword())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Username not found / incorrect password")));
    }

    @Test
    public void login_WrongPassword_ShouldThrowCustomException() throws Exception {

        UserDto user = new UserDto(USER, "wrongPassword");

        given(userService.login(user))
                .willThrow(new CustomException("Not Found",
                        "Username Not Found / Incorrect Password", HttpStatus.NOT_FOUND));

        mvc.perform(post(API_AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(user.getUsername(), user.getPassword())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Username not found / incorrect password")));
    }

    // Register: success
    @Test
    public void register_ShouldReturnOk() throws Exception {
        User user = new User("user@mail.com", "password1234");

        given(userService.register(user))
                .willReturn("ok");

        MvcResult result = mvc.perform(post(API_AUTH + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(user.getUsername(), user.getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("ok", response);
    }

    // Register: fail
    @Test
    public void register_InvalidUsername_ShouldReturnBadRequest() throws Exception {
        String body = requestBody("notValidUsername", "password");

        mvc.perform(post(API_AUTH + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("username", containsInAnyOrder("Not a valid email address")));
    }

    @Test
    public void register_InvalidPwd_ShouldReturnBadRequest() throws Exception {
        String body = requestBody("username@mail.com", "1");

        mvc.perform(post(API_AUTH + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("password", containsInAnyOrder("Password is required")));
    }


    // Token: fail
    @Test
    public void getToken_NotFound_ShouldThrowCustomException() throws Exception {
        
        final String TOKEN = "tokenDoesNotExist";

        given(userService.generateNewTokens(TOKEN))
                .willThrow(new CustomException(
                        "Refresh token is not valid or expired, please, try to log in",
                        HttpStatus.BAD_REQUEST));

        mvc.perform(post(API_AUTH + "token")
                .header("token", TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("Refresh token is not valid or expired, please, try to log in")));
    }

    @Test
    public void getToken_ShouldReturnNewTokens() throws Exception {

        given(userService.generateNewTokens("refreshToken"))
                .willReturn("access_token, refresh_token");

        String response = mvc.perform(post(API_AUTH + "token")
                .header("token", "refreshToken"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(response, "access_token, refresh_token");
    }

}

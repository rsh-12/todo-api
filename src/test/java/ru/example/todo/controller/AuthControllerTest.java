package ru.example.todo.controller;
/*
 * Date: 5/15/21
 * Time: 6:25 PM
 * */

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;
import ru.example.todo.facade.PasswordFacade;
import ru.example.todo.messaging.MessagingService;
import ru.example.todo.messaging.requests.EmailRequest;
import ru.example.todo.messaging.requests.TokenRequest;
import ru.example.todo.service.UserService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTestClass {

    @MockBean
    private PasswordFacade passwordFacade;

    @MockBean
    private UserService userService;

    @SpyBean
    private MessagingService messagingService;

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

        given(userService.login(Mockito.any(UserDto.class)))
                .willReturn("access_token");

        String response = mvc.perform(post(API_AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(admin.getUsername(), admin.getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("access_token"));
        verify(userService, times(1)).login(Mockito.any());
    }

    // Login: fail
    @Test
    public void login_NotFound_ShouldThrowCustomException() throws Exception {
        UserDto user = new UserDto("usernameNotExists@mail.com", "somePassword");

        given(userService.login(Mockito.any(UserDto.class)))
                .willThrow(new CustomException("Not Found",
                        "Username Not Found / Incorrect Password", HttpStatus.NOT_FOUND));

        mvc.perform(post(API_AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(user.getUsername(), user.getPassword())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Username not found / incorrect password")));

        verify(userService, times(1)).login(Mockito.any(UserDto.class));
    }

    @Test
    public void login_WrongPassword_ShouldThrowCustomException() throws Exception {
        UserDto user = new UserDto(USER, "wrongPassword");

        given(userService.login(Mockito.any(UserDto.class)))
                .willThrow(new CustomException("Not Found",
                        "Username Not Found / Incorrect Password", HttpStatus.NOT_FOUND));

        mvc.perform(post(API_AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(user.getUsername(), user.getPassword())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("Username not found / incorrect password")));

        verify(userService, times(1)).login(Mockito.any(UserDto.class));
    }

    // Register: success
    @Test
    public void register_ShouldReturnOk() throws Exception {
        User user = new User("user@mail.com", "password1234");

        given(userService.register(Mockito.any(User.class)))
                .willReturn("ok");

        MvcResult result = mvc.perform(post(API_AUTH + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(user.getUsername(), user.getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("ok", response);

        verify(userService, times(1)).register(Mockito.any(User.class));
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

        given(userService.generateNewTokens(Mockito.anyString()))
                .willThrow(new CustomException(
                        "Refresh token is not valid or expired, please, try to log in",
                        HttpStatus.BAD_REQUEST));

        mvc.perform(post(API_AUTH + "token")
                .header("token", TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("Refresh token is not valid or expired, please, try to log in")));

        verify(userService, times(1)).generateNewTokens(Mockito.anyString());
    }

    @Test
    public void getToken_ShouldReturnNewTokens() throws Exception {
        given(userService.generateNewTokens(Mockito.anyString()))
                .willReturn("access_token, refresh_token");

        String response = mvc.perform(post(API_AUTH + "token")
                .header("token", "refreshToken"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(response, "access_token, refresh_token");
        verify(userService, times(1)).generateNewTokens(Mockito.anyString());
    }

    @Test
    public void sendPasswordResetToken_ShouldReturnStatusOk() throws Exception {
        String email = "test@mail.com";
        doNothing().when(messagingService).send(Mockito.any(EmailRequest.class));

        JSONObject body = new JSONObject();
        body.put("email", email);

        mvc.perform(post(API_AUTH + "password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(messagingService, times(1)).send(Mockito.any(EmailRequest.class));
    }

    @Test
    public void sendPasswordResetToken_ShouldThrowCustomExceptionAndReturnNotFound() throws Exception {
        String email = "test@mail.com";
        doThrow(new CustomException("Not Found", "Username Not Found", HttpStatus.NOT_FOUND))
                .when(messagingService).send(Mockito.any(EmailRequest.class));

        JSONObject body = new JSONObject();
        body.put("email", email);

        mvc.perform(post(API_AUTH + "password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("username not found")));

        verify(messagingService, times(1)).send(Mockito.any(EmailRequest.class));
    }


    @Test
    public void updatePassword_ShouldReturnStatusOk() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("password", "somePassword");

        doNothing().when(passwordFacade).updatePassword(Mockito.any(TokenRequest.class), Mockito.anyString());

        mvc.perform(post(API_AUTH + "password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "someToken")
                .content(convertToJson(body)))
                .andExpect(status().isOk());

        verify(passwordFacade, times(1)).updatePassword(Mockito.any(TokenRequest.class), Mockito.anyString());
    }

}

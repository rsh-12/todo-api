package ru.example.todoapp.controller;
/*
 * Date: 5/15/21
 * Time: 6:25 PM
 * */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import ru.example.todoapp.controller.request.CredentialsRequest;
import ru.example.todoapp.controller.request.EmailRequest;
import ru.example.todoapp.controller.request.PasswordRequest;
import ru.example.todoapp.controller.request.TokenRequest;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.PasswordFacade;
import ru.example.todoapp.messaging.MessagingClient;
import ru.example.todoapp.service.AuthService;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTestClass {

    @MockBean
    private PasswordFacade passwordFacade;

    @MockBean
    private AuthService authService;

    @SpyBean
    private MessagingClient messagingService;

    private static final String API_AUTH = "/api/auth";
    private static final String USERNAME = "username@mail.com";
    private static final String PASSWORD = "password12";

    @Test
    @DisplayName("login: returns tokens, 200 ok")
    public void login_ShouldReturnTokens() throws Exception {
        String accessToken = "someAccessToken";
        given(authService.login(any(CredentialsRequest.class), anyString()))
                .willReturn(Map.of("access_token", accessToken));

        mvc.perform(post(API_AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestOf(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token", containsString(accessToken)));

        verify(authService).login(any(), anyString());
    }

    @Test
    @DisplayName("login: throws CustomException, returns notFound")
    public void login_NotFound_ShouldThrowCustomException() throws Exception {
        given(authService.login(any(CredentialsRequest.class), anyString()))
                .willThrow(CustomException.notFound("Username not found/Incorrect Password"));

        mvc.perform(post(API_AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestOf(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsString("Username not found")));
    }

    @Test
    @DisplayName("login: throws CustomException, returns notFound")
    public void login_WrongPassword_ShouldThrowCustomException() throws Exception {
        given(authService.login(any(), anyString()))
                .willThrow(CustomException.notFound("Username not found"));

        mvc.perform(post(API_AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestOf(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsString("Username not found")));
    }

    @Test
    @DisplayName("register: returns created")
    public void register_ShouldReturnOk() throws Exception {
        User user = mock(User.class);
        given(user.getUsername()).willReturn("username@mail.com");
        given(user.getPassword()).willReturn("password");
        given(user.getCreatedAt()).willReturn(LocalDateTime.now());

        given(authService.register(any())).willReturn(user);

        mvc.perform(post(API_AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestOf(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("register: returns badRequest")
    public void register_InvalidUsername_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestOf("notValidUsername", PASSWORD)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("username", containsInAnyOrder("Not a valid email address")));
    }

    @Test
    @DisplayName("register: returns badRequest")
    public void register_InvalidPwd_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestOf(USERNAME, "p")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("password", containsInAnyOrder("Password is required")));
    }

    @Test
    @DisplayName("getToken: throws CustomException, returns notFound")
    public void getToken_NotFound_ShouldThrowCustomException() throws Exception {
        String errorMessage = "Refresh token owner not found";
        given(authService.generateNewTokens(anyString(), anyString()))
                .willThrow(CustomException.notFound(errorMessage));

        mvc.perform(post(API_AUTH + "/token")
                .header("token", "Beare someAccessToken"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message",
                        containsString(errorMessage)));
    }

    @Test
    @DisplayName("getToken: returns tokens, 200 ok")
    public void getToken_ShouldReturnNewTokens() throws Exception {
        given(authService.generateNewTokens(anyString(), anyString()))
                .willReturn(Map.of("access_token", "access_token", "refresh_token", "refresh_token"));

        mvc.perform(post(API_AUTH + "/token")
                .header("token", "refreshToken"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token", containsString("access_token")))
                .andExpect(jsonPath("refresh_token", containsString("refresh_token")));
    }

    @Test
    @DisplayName("sendPasswordResetToken: returns 200 ok")
    public void sendPasswordResetToken_ShouldReturnStatusOk() throws Exception {
        doNothing().when(messagingService).send(any(EmailRequest.class));

        String body = objectMapper.writeValueAsString(new EmailRequest("test@mail.com"));
        mvc.perform(post(API_AUTH + "/password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("sendPasswordResetToken: throws CustomException, returns notFound")
    public void sendPasswordResetToken_ShouldThrowCustomExceptionAndReturnNotFound() throws Exception {
        doThrow(CustomException.notFound("Username not found")).when(messagingService).send(any(EmailRequest.class));

        String body = objectMapper.writeValueAsString(new EmailRequest("test@mail.com"));
        mvc.perform(post(API_AUTH + "/password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsString("Username not found")));
    }

    @Test
    @DisplayName("sendPasswordResetToken: throws CustomException, returns internalServerError")
    public void sendPasswordResetToken_ShouldThrowCustomExceptionAndReturnInternalServerError() throws Exception {
        doThrow(CustomException.internalServerError("An error occurred while generating the token"))
                .when(messagingService).send(any(EmailRequest.class));

        String body = objectMapper.writeValueAsString(new EmailRequest("test@mail.com"));
        mvc.perform(post(API_AUTH + "/password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("message",
                        containsString("An error occurred while generating the token")));
    }

    @Test
    @DisplayName("updatePassword: returns 200 ok")
    public void updatePassword_ShouldReturnStatusOk() throws Exception {
        PasswordRequest request = new PasswordRequest("password12345");
        doNothing().when(passwordFacade).updatePassword(any(TokenRequest.class), anyString());

        mvc.perform(post(API_AUTH + "/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "someToken")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("updatePassword: returns badRequest")
    public void updatePassword_PasswordIsNull_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_AUTH + "/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "someToken")
                .content(Map.of("something", "wrong").toString()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(passwordFacade);
    }

    @Test
    @DisplayName("updatePassword: returns badRequest")
    public void updatePassword_TokenIsNull_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_AUTH + "/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PasswordRequest("password"))))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(passwordFacade);
    }

}

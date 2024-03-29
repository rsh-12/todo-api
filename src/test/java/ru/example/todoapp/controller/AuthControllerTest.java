package ru.example.todoapp.controller;
/*
 * Date: 5/15/21
 * Time: 6:25 PM
 * */

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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import ru.example.todoapp.domain.request.CredentialsRequest;
import ru.example.todoapp.domain.request.EmailRequest;
import ru.example.todoapp.domain.request.PasswordRequest;
import ru.example.todoapp.domain.request.TokenRequest;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.exception.NotFoundException;
import ru.example.todoapp.facade.PasswordFacade;
import ru.example.todoapp.messaging.MessagingClient;
import ru.example.todoapp.service.AuthService;

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
        Map<String, String> tokens = Map.of("access_token", accessToken);
        given(authService.login(any(CredentialsRequest.class), anyString()))
                .willReturn(Optional.of(tokens));

        mvc.perform(post(API_AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestOf(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token", containsString(accessToken)));

        verify(authService).login(any(), anyString());
    }

    @Test
    @DisplayName("login: returns empty, notFound")
    public void login_NotFound_ShouldReturnEmpty() throws Exception {
        given(authService.login(any(CredentialsRequest.class), anyString()))
                .willReturn(Optional.empty());

        mvc.perform(post(API_AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestOf(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("login: returns empty, notFound")
    public void login_WrongPassword_ShouldReturnEmpty() throws Exception {
        given(authService.login(any(), anyString()))
                .willReturn(Optional.empty());

        mvc.perform(post(API_AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestOf(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("register: returns created")
    public void register_ShouldReturnCreated() throws Exception {
        User user = mock(User.class);
        given(user.getUsername()).willReturn(USERNAME);
        given(user.getPassword()).willReturn(PASSWORD);
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
                .andExpect(jsonPath("username", containsInAnyOrder("Email validation error")));
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
    @DisplayName("getTokens: returns empty and notFound")
    public void getTokens_NotFound_ShouldReturnEmpty() throws Exception {
        given(authService.generateNewTokens(anyString(), anyString()))
                .willReturn(Optional.empty());

        mvc.perform(post(API_AUTH + "/token")
                        .header("token", "Bearer someAccessToken"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getTokens: returns tokens, 200 ok")
    public void getTokens_ShouldReturnNewTokens() throws Exception {
        Map<String, String> tokens = Map.of("access_token", "access_token", "refresh_token",
                "refresh_token");
        given(authService.generateNewTokens(anyString(), anyString()))
                .willReturn(Optional.of(tokens));

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
    @DisplayName("sendPasswordResetToken: throws NotFoundException, returns notFound")
    public void sendPasswordResetToken_ShouldThrowExceptionAndReturnNotFound() throws Exception {
        doThrow(new NotFoundException("Username not found")).when(messagingService)
                .send(any(EmailRequest.class));

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
    public void sendPasswordResetToken_ShouldThrowExceptionAndReturnInternalServerError()
            throws Exception {
        doThrow(CustomException.createInternalServerErrorExc(
                "An error occurred while generating the token"))
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

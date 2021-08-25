package ru.example.todoapp.controller;
/*
 * Date: 5/15/21
 * Time: 6:25 PM
 * */

import org.json.JSONObject;
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
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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

    // Login: fail
    @Test
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

    // Register: success
    @Test
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

    // Register: fail
    @Test
    public void register_InvalidUsername_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestOf("notValidUsername", PASSWORD)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("username", containsInAnyOrder("Not a valid email address")));
    }

    @Test
    public void register_InvalidPwd_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestOf(USERNAME, "p")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("password", containsInAnyOrder("Password is required")));
    }

    // Token: fail
    @Test
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
    public void sendPasswordResetToken_ShouldReturnStatusOk() throws Exception {
        String email = "test@mail.com";
        doNothing().when(messagingService).send(any(EmailRequest.class));

        String body = objectMapper.writeValueAsString(Map.of("email", email));
        mvc.perform(post(API_AUTH + "/password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void sendPasswordResetToken_ShouldThrowCustomExceptionAndReturnNotFound() throws Exception {
        String email = "test@mail.com";
        doThrow(CustomException.notFound("Username Not Found")).when(messagingService).send(any(EmailRequest.class));

        JSONObject body = new JSONObject();
        body.put("email", email);

        mvc.perform(post(API_AUTH + "/password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("username not found")));

        verify(messagingService, times(1)).send(any(EmailRequest.class));
    }

    @Test
    public void sendPasswordResetToken_ShouldThrowCustomExceptionAndReturnInternalServerError() throws Exception {
        String email = "test@mail.com";
        doThrow(CustomException.internalServerError("An error occurred while generating the token"))
                .when(messagingService).send(any(EmailRequest.class));

        JSONObject body = new JSONObject();
        body.put("email", email);

        mvc.perform(post(API_AUTH + "/password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString()))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("An error occurred while generating the token")));

        verify(messagingService, times(1)).send(any(EmailRequest.class));
    }

    @Test
    public void updatePassword_ShouldReturnStatusOk() throws Exception {
        PasswordRequest request = new PasswordRequest("password12345");
        doNothing().when(passwordFacade).updatePassword(any(TokenRequest.class), anyString());

        mvc.perform(post(API_AUTH + "/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "someToken")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(passwordFacade, times(1)).updatePassword(any(TokenRequest.class), anyString());
    }

    @Test
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
    public void updatePassword_TokenIsNull_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_AUTH + "/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PasswordRequest("password"))))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(passwordFacade);
    }

}

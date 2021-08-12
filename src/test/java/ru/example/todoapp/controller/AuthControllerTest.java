package ru.example.todoapp.controller;
/*
 * Date: 5/15/21
 * Time: 6:25 PM
 * */

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import ru.example.todoapp.controller.request.CredentialsRequest;
import ru.example.todoapp.dto.UserDto;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.PasswordFacade;
import ru.example.todoapp.messaging.MessagingClient;
import ru.example.todoapp.controller.request.EmailRequest;
import ru.example.todoapp.controller.request.TokenRequest;
import ru.example.todoapp.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private UserService userService;

    @SpyBean
    private MessagingClient messagingService;

    // Login: success
    @Test
    public void login_ShouldReturnTokens() throws Exception {
        CredentialsRequest request = new CredentialsRequest("username@mail.ru", "password");

        given(userService.login(any(CredentialsRequest.class), anyString()))
                .willReturn(Map.of("access_token", "access_token"));

        String response = mvc.perform(post(API_AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usernamePasswordRequestBody(request.username(), request.password())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("access_token"));
        verify(userService, times(1)).login(any(), anyString());
    }

    // Login: fail
    @Test
    public void login_NotFound_ShouldThrowCustomException() throws Exception {
        CredentialsRequest request = new CredentialsRequest("username@mail.ru", "password");

        given(userService.login(any(CredentialsRequest.class), anyString()))
                .willThrow(CustomException.notFound("Username Not Found/Incorrect Password"));

        mvc.perform(post(API_AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usernamePasswordRequestBody(request.username(), request.password())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("Username not found/incorrect password")));

        verify(userService, times(1)).login(any(CredentialsRequest.class), anyString());
    }

    @Test
    public void login_WrongPassword_ShouldThrowCustomException() throws Exception {
        CredentialsRequest request = new CredentialsRequest("username@mail.ru", "password");

        given(userService.login(any(CredentialsRequest.class), anyString()))
                .willThrow(CustomException.notFound("Username Not Found/Incorrect Password"));

        mvc.perform(post(API_AUTH + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usernamePasswordRequestBody(request.username(), request.password())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("Username not found/incorrect password")));

        verify(userService, times(1)).login(any(CredentialsRequest.class), anyString());
    }

    // Register: success
    @Test
    public void register_ShouldReturnOk() throws Exception {
        User user = mock(User.class);
        given(user.getUsername()).willReturn("username@mail.com");
        given(user.getPassword()).willReturn("password");
        given(user.getCreatedAt()).willReturn(LocalDateTime.now());

        given(userService.register(any())).willReturn(user);
        given(userService.mapToUserDto(any(User.class)))
                .willReturn(new UserDto("username", LocalDateTime.now()));

        mvc.perform(post(API_AUTH + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usernamePasswordRequestBody(user.getUsername(), user.getPassword())))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(userService, times(1)).register(any(CredentialsRequest.class));
    }

    // Register: fail
    @Test
    public void register_InvalidUsername_ShouldReturnBadRequest() throws Exception {
        String body = usernamePasswordRequestBody("notValidUsername", "password");

        mvc.perform(post(API_AUTH + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("username", containsInAnyOrder("Not a valid email address")));
    }

    @Test
    public void register_InvalidPwd_ShouldReturnBadRequest() throws Exception {
        String body = usernamePasswordRequestBody("username@mail.com", "1");

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

        given(userService.generateNewTokens(anyString(), anyString()))
                .willThrow(CustomException.badRequest("Refresh token is not valid or expired, please, try to log in"));

        mvc.perform(post(API_AUTH + "token")
                .header("token", TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message",
                        containsStringIgnoringCase("Refresh token is not valid or expired, please, try to log in")));

        verify(userService, times(1)).generateNewTokens(anyString(), anyString());
    }

    @Test
    public void getToken_ShouldReturnNewTokens() throws Exception {
        given(userService.generateNewTokens(anyString(), anyString()))
                .willReturn(Map.of("access_token", "access_token", "refresh_token", "refresh_token"));

        String response = mvc.perform(post(API_AUTH + "token")
                .header("token", "refreshToken"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("access_token") && response.contains("refresh_token"));
        verify(userService, times(1)).generateNewTokens(anyString(), anyString());
    }

    @Test
    public void sendPasswordResetToken_ShouldReturnStatusOk() throws Exception {
        String email = "test@mail.com";
        doNothing().when(messagingService).send(any(EmailRequest.class));

        JSONObject body = new JSONObject();
        body.put("email", email);

        mvc.perform(post(API_AUTH + "password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(messagingService, times(1)).send(any(EmailRequest.class));
    }

    @Test
    public void sendPasswordResetToken_ShouldThrowCustomExceptionAndReturnNotFound() throws Exception {
        String email = "test@mail.com";
        doThrow(CustomException.notFound("Username Not Found")).when(messagingService).send(any(EmailRequest.class));

        JSONObject body = new JSONObject();
        body.put("email", email);

        mvc.perform(post(API_AUTH + "password/forgot")
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

        mvc.perform(post(API_AUTH + "password/forgot")
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
        Map<String, String> body = new HashMap<>();
        body.put("password", "somePassword");

        doNothing().when(passwordFacade).updatePassword(any(TokenRequest.class), anyString());

        mvc.perform(post(API_AUTH + "password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "someToken")
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(passwordFacade, times(1)).updatePassword(any(TokenRequest.class), anyString());
    }

    @Test
    public void updatePassword_PasswordIsNull_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_AUTH + "password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", "someToken")
                .content(Map.of("something", "wrong").toString()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(passwordFacade);
    }

    @Test
    public void updatePassword_TokenIsNull_ShouldReturnBadRequest() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("password", "somePassword");

        mvc.perform(post(API_AUTH + "password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(passwordFacade);
    }

}

package ru.example.todoapp.controller;
/*
 * Date: 3/26/21
 * Time: 2:10 PM
 * */

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.service.UserService;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends AbstractControllerTestClass {

    @MockBean
    private UserService userService;

    private static final String API_USERS = "/api/users";

    @Test
    @WithUserDetails(ADMIN)
    public void getUser_ShouldReturnUser() throws Exception {
        User user = new User("user", "password");
        given(userService.findUserById(anyLong()))
                .willReturn(Optional.of(user));

        mvc.perform(get(API_USERS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is("user")))
                .andDo(print());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void getUser_ShouldReturnEmpty() throws Exception {
        given(userService.findUserById(anyLong())).willReturn(Optional.empty());

        mvc.perform(get(API_USERS + "/1"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithUserDetails(USER)
    public void getUser_ShouldReturnForbidden() throws Exception {
        mvc.perform(get(API_USERS + "/1"))
                .andExpect(status().isForbidden())
                .andDo(print());

        verify(userService, times(0)).findUserById(anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUserById(anyLong());

        mvc.perform(delete(API_USERS + "/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void deleteUser_ShouldReturnNotFound() throws Exception {
        doThrow(CustomException.notFound("User Not Found")).when(userService).deleteUserById(anyLong());

        mvc.perform(delete(API_USERS + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", containsStringIgnoringCase("not found")))
                .andDo(print());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteUser_ShouldReturnForbidden() throws Exception {
        mvc.perform(delete(API_USERS + "/1"))
                .andExpect(status().isForbidden())
                .andDo(print());

        verify(userService).deleteUserById(anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updatePassword_ShouldReturnOk() throws Exception {
        given(userService.updatePassword(anyString(), anyString()))
                .willReturn(Optional.of(new User(USER, "password12")));

        mvc.perform(post(API_USERS + "/password/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Map.of("password", "somePassword").toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updatePassword_ShouldReturnNotFound() throws Exception {
        given(userService.updatePassword(anyString(), anyString()))
                .willReturn(Optional.empty());

        mvc.perform(post(API_USERS + "/password/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Map.of("password", "somePassword").toString()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updatePassword_NoBody_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_USERS + "/password/update")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

}

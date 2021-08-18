package ru.example.todoapp.controller;
/*
 * Date: 3/26/21
 * Time: 2:10 PM
 * */

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.service.UserService;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest extends AbstractControllerTestClass {

    @MockBean
    private UserService userService;

    @Test
    @WithUserDetails(ADMIN)
    public void getUser_ShouldReturnUser() throws Exception {
        given(userService.findUserById(anyLong()))
                .willReturn(new User("user", "password"));

        mvc.perform(get(API_USERS + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is("user")))
                .andDo(print());

        verify(userService, times(1)).findUserById(anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void getUser_ShouldReturnNotFound() throws Exception {
        given(userService.findUserById(anyLong())).willThrow(CustomException.notFound("User Not Found"));

        mvc.perform(get(API_USERS + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", containsStringIgnoringCase("not found")))
                .andDo(print());

        verify(userService, times(1)).findUserById(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void getUser_ShouldReturnForbidden() throws Exception {
        mvc.perform(get(API_USERS + 1))
                .andExpect(status().isForbidden())
                .andDo(print());

        verify(userService, times(0)).findUserById(anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUserById(anyLong());

        mvc.perform(delete(API_USERS + 1))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(userService, times(1)).deleteUserById(anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void deleteUser_ShouldReturnNotFound() throws Exception {
        doThrow(CustomException.notFound("User Not Found")).when(userService).deleteUserById(anyLong());

        mvc.perform(delete(API_USERS + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", containsStringIgnoringCase("not found")))
                .andDo(print());

        verify(userService, times(1)).deleteUserById(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteUser_ShouldReturnForbidden() throws Exception {
        mvc.perform(delete(API_USERS + 1))
                .andExpect(status().isForbidden())
                .andDo(print());

        verify(userService, times(0)).deleteUserById(anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updatePassword_ShouldReturnOk() throws Exception {
        doNothing().when(userService).updatePassword(anyString(), anyString());

        JSONObject body = new JSONObject();
        body.put("password", "somePassword");

        mvc.perform(post(API_USERS + "password/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body.toString())))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).updatePassword(anyString(), anyString());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updatePassword_ServiceException_ShouldReturnBadRequest() throws Exception {
        doThrow(CustomException.badRequest("Invalid data"))
                .when(userService).updatePassword(anyString(), anyString());

        JSONObject body = new JSONObject();
        body.put("password", "somePassword");

        mvc.perform(post(API_USERS + "password/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body.toString())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", containsStringIgnoringCase("bad request")));

        verify(userService, times(1)).updatePassword(anyString(), anyString());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updatePassword_NoBody_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_USERS + "password/update")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).updatePassword(anyString(), anyString());
    }

}

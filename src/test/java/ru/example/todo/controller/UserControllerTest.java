package ru.example.todo.controller;
/*
 * Date: 3/26/21
 * Time: 2:10 PM
 * */

import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo update tests, mock service layer
public class UserControllerTest extends AbstractControllerTestClass {

    // Delete user: success
    @Test
    @WithUserDetails(ADMIN)
    public void deleteUser_ShouldReturnNoContent() throws Exception {
        final int USER_ID = 4;

        mvc.perform(delete(API_USERS + USER_ID))
                .andExpect(status().isNoContent());

        mvc.perform(delete(API_USERS + USER_ID))
                .andExpect(status().isNotFound());
    }

    // Delete user:  fail
    @Test
    @WithUserDetails(USER)
    public void deleteUser_ShouldReturnForbidden() throws Exception {
        final int USER_ID = 4;

        mvc.perform(delete(API_USERS + USER_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Not enough permissions")));

    }

}

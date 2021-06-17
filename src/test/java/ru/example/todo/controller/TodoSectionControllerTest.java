package ru.example.todo.controller;
/*
 * Date: 3/12/21
 * Time: 10:30 PM
 * */

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todo.domain.CustomPrincipal;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;
import ru.example.todo.facade.TasksFacade;
import ru.example.todo.service.TodoSectionService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TodoSectionControllerTest extends AbstractControllerTestClass {

    @MockBean
    private TodoSectionService sectionService;

    @MockBean
    private TasksFacade tasksFacade;

    // get all sections
    @Test
    @WithUserDetails(ADMIN)
    public void getSections_ShouldReturnListOfSections() throws Exception {
        given(sectionService.findSectionDtoList(any(CustomPrincipal.class)))
                .willReturn(List.of(new TodoSectionDto("section1"), new TodoSectionDto("section2")));

        mvc.perform(get(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections[0].title", is("section1")))
                .andExpect(jsonPath("_embedded.sections[1].title", is("section2")))
                .andDo(print());

        verify(sectionService, times(1)).findSectionDtoList(any(CustomPrincipal.class));
    }

    @Ignore
    @Test
    @WithUserDetails(ADMIN)
    public void getSections_ShouldReturnEmptyList() throws Exception {
        given(sectionService.findSectionDtoList(any(CustomPrincipal.class)))
                .willReturn(Collections.emptyList());

        mvc.perform(get(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections.isEmpty()", is(true)))
                .andDo(print());

        verify(sectionService, times(1)).findSectionDtoList(any(CustomPrincipal.class));
    }

    // get section by ID
    @Test
    @WithUserDetails(ADMIN)
    public void getSection_ShouldReturnSectionById() throws Exception {
        given(sectionService.findSectionById(any(CustomPrincipal.class), anyLong()))
                .willReturn(new TodoSection("Important"));

        mvc.perform(get(API_SECTIONS + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Important")))
                .andDo(print());

        verify(sectionService, times(1)).findSectionById(any(CustomPrincipal.class), anyLong());
    }

    // get section by non-existent id: returns 404 NOT FOUND
    @Test
    @WithUserDetails(USER)
    public void getSection_ShouldReturnBadRequest() throws Exception {
        given(sectionService.findSectionById(any(CustomPrincipal.class), anyLong()))
                .willThrow(new CustomException("Not Found", "Section not found: " + 1, HttpStatus.NOT_FOUND));

        mvc.perform(get(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("section not found")));

        verify(sectionService, times(1)).findSectionById(any(CustomPrincipal.class), anyLong());
    }

    // delete section
    @Test
    @WithUserDetails(ADMIN)
    public void deleteSection_ShouldDeleteSectionById() throws Exception {
        doNothing().when(sectionService).deleteSectionById(any(CustomPrincipal.class), anyLong());

        // delete by id: returns 204 NO CONTENT
        mvc.perform(delete(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(sectionService, times(1)).deleteSectionById(any(CustomPrincipal.class), anyLong());
    }

    // delete section by non-existent id: returns 204 NO CONTENT
    @Test
    @WithUserDetails(USER)
    public void deleteSection_ShouldReturnNotFound() throws Exception {
        doThrow(new CustomException("Not Found", "Section not found", HttpStatus.NOT_FOUND))
                .when(sectionService).deleteSectionById(any(CustomPrincipal.class), anyLong());

        mvc.perform(delete(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", containsStringIgnoringCase("not found")))
                .andDo(print());

        verify(sectionService, times(1)).deleteSectionById(any(CustomPrincipal.class), anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteSection_ShouldReturnForbidden() throws Exception {
        doThrow(new CustomException("Forbidden", "Not enough permissions", HttpStatus.FORBIDDEN))
                .when(sectionService).deleteSectionById(any(CustomPrincipal.class), anyLong());

        mvc.perform(delete(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error", containsStringIgnoringCase("Forbidden")))
                .andDo(print());

        verify(sectionService, times(1)).deleteSectionById(any(CustomPrincipal.class), anyLong());
    }

    // create new section
    @Test
    @WithUserDetails(ADMIN)
    public void createSection_ShouldReturnStatusCreated() throws Exception {
        doNothing().when(sectionService).createSection(any(User.class), any(TodoSectionDto.class));

        mvc.perform(post(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(new TodoSectionDto("Created Section"))))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(sectionService, times(1)).createSection(any(User.class), any(TodoSectionDto.class));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void createSection_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(sectionService, times(0)).createSection(any(User.class), any(TodoSectionDto.class));
    }

    // update section
    @Test
    @WithUserDetails(ADMIN)
    public void updateSection_ShouldReturnOk() throws Exception {
        doNothing().when(sectionService)
                .updateSection(any(CustomPrincipal.class), anyLong(), any(TodoSectionDto.class));

        // update section by id: returns 200 OK
        mvc.perform(put(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(new TodoSectionDto("Title"))))
                .andExpect(status().isOk())
                .andDo(print());

        verify(sectionService, times(1))
                .updateSection(any(CustomPrincipal.class), anyLong(), any(TodoSectionDto.class));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updateSection_ShouldReturnNotFound() throws Exception {
        doThrow(new CustomException("Not Found", "Section Not Found", HttpStatus.NOT_FOUND))
                .when(sectionService)
                .updateSection(any(CustomPrincipal.class), anyLong(), any(TodoSectionDto.class));

        mvc.perform(put(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(new TodoSectionDto("Title"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", containsStringIgnoringCase("not found")))
                .andDo(print());

        verify(sectionService, times(1))
                .updateSection(any(CustomPrincipal.class), anyLong(), any(TodoSectionDto.class));
    }

    @Test
    @WithUserDetails(USER)
    public void updateSection_ShouldReturnForbidden() throws Exception {
        doThrow(new CustomException("Forbidden", "Not enough permissions", HttpStatus.FORBIDDEN))
                .when(sectionService)
                .updateSection(any(CustomPrincipal.class), anyLong(), any(TodoSectionDto.class));

        mvc.perform(put(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(new TodoSectionDto("Title"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error", containsStringIgnoringCase("forbidden")))
                .andDo(print());

        verify(sectionService, times(1))
                .updateSection(any(CustomPrincipal.class), anyLong(), any(TodoSectionDto.class));
    }

    @Test
    @WithUserDetails(USER)
    public void updateSection_ShouldReturnBadRequest() throws Exception {
        mvc.perform(put(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(sectionService, times(0))
                .updateSection(any(CustomPrincipal.class), anyLong(), any(TodoSectionDto.class));
    }

    @Test
    @WithUserDetails(USER)
    public void addOrRemoveTasks_ShouldReturnOk() throws Exception {
        doNothing().when(tasksFacade)
                .addTasksToOrRemoveFromSection(anyLong(), anyLong(), anySet(), any());

        Map<String, Integer[]> body = new WeakHashMap<>();
        body.put("tasks", new Integer[]{1, 2});

        mvc.perform(post(API_SECTIONS + "1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "move")
                .content(convertToJson(body)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(tasksFacade, times(1))
                .addTasksToOrRemoveFromSection(anyLong(), anyLong(), anySet(), any());
    }

    @Test
    @WithUserDetails(USER)
    public void addOrRemoveTasks_EmptyBody_ShouldReturnBadRequest() throws Exception {
        doNothing().when(tasksFacade)
                .addTasksToOrRemoveFromSection(anyLong(), anyLong(), anySet(), any());

        mvc.perform(post(API_SECTIONS + "1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "move"))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(tasksFacade, times(0))
                .addTasksToOrRemoveFromSection(anyLong(), anyLong(), anySet(), any());
    }

    @Test
    @WithUserDetails(USER)
    public void addOrRemoveTasks_NoParam_ShouldReturnBadRequest() throws Exception {
        doNothing().when(tasksFacade)
                .addTasksToOrRemoveFromSection(anyLong(), anyLong(), anySet(), any());

        Map<String, Integer[]> body = new WeakHashMap<>();
        body.put("tasks", new Integer[]{1, 2});

        mvc.perform(post(API_SECTIONS + "1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(body)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(tasksFacade, times(0))
                .addTasksToOrRemoveFromSection(anyLong(), anyLong(), anySet(), any());
    }

}

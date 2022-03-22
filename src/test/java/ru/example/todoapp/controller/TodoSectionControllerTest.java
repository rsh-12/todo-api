package ru.example.todoapp.controller;
/*
 * Date: 3/12/21
 * Time: 10:30 PM
 * */

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todoapp.domain.request.TodoSectionRequest;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.exception.NotFoundException;
import ru.example.todoapp.facade.TasksFacade;
import ru.example.todoapp.service.dto.TodoSectionDto;
import ru.example.todoapp.service.impl.TodoSectionServiceImpl;
import ru.example.todoapp.service.mapper.SectionMapper;


public class TodoSectionControllerTest extends AbstractControllerTestClass {

    @MockBean
    private TodoSectionServiceImpl sectionService;

    @MockBean
    private TasksFacade tasksFacade;

    @MockBean
    private SectionMapper sectionMapper;

    private static final String API_SECTIONS = "/api/sections/";

    @Test
    @WithUserDetails(ADMIN)
    public void getSections_ShouldReturnEmptyList() throws Exception {
        Page<TodoSection> resultPage = new PageImpl<>(Collections.emptyList());
        given(sectionService.findAll(any()))
                .willReturn(resultPage);

        String response = mvc.perform(get(API_SECTIONS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("page"));
        assumeTrue(response.contains("tasks"), "field tasks not found");
    }

    // get section by ID
    @Test
    @WithUserDetails(ADMIN)
    public void getSection_ShouldReturnSectionById() throws Exception {
        TodoSection section = mock(TodoSection.class);
        given(sectionService.findOne(1L)).willReturn(Optional.of(section));

        TodoSectionDto sectionDto = new TodoSectionDto(1L, "Important", LocalDateTime.now(), LocalDateTime.now());
        given(sectionMapper.mapToSectionDto(any(TodoSection.class))).willReturn(sectionDto);

        mvc.perform(get(API_SECTIONS + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Important")))
                .andDo(print());
    }

    // get section by non-existent id: returns 404 NOT FOUND
    @Test
    @WithUserDetails(USER)
    public void getSection_ShouldReturnBadRequest() throws Exception {
        given(sectionService.findOne(anyLong())).willReturn(Optional.empty());

        mvc.perform(get(API_SECTIONS + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // delete section
    @Test
    @WithUserDetails(ADMIN)
    public void deleteSection_ShouldDeleteSectionById() throws Exception {
        doNothing().when(sectionService).delete(anyLong());

        // delete by id: returns 204 NO CONTENT
        mvc.perform(delete(API_SECTIONS + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(sectionService).delete(anyLong());
    }

    // delete section by non-existent id: returns 204 NO CONTENT
    @Test
    @WithUserDetails(USER)
    public void deleteSection_ShouldReturnNotFound() throws Exception {
        doThrow(new NotFoundException("Section not found"))
                .when(sectionService).delete(anyLong());

        mvc.perform(delete(API_SECTIONS + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(sectionService).delete(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteSection_ShouldReturnForbidden() throws Exception {
        doThrow(CustomException.createForbiddenExc("Not enough permissions"))
                .when(sectionService).delete(anyLong());

        mvc.perform(delete(API_SECTIONS + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error", containsStringIgnoringCase("Forbidden")))
                .andDo(print());

        verify(sectionService).delete(anyLong());
    }

    @Test
    @WithUserDetails(ADMIN)
    public void createSection_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_SECTIONS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(sectionService);
    }

    // update section
    @Test
    @WithUserDetails(ADMIN)
    public void updateSection_ShouldReturnOk() throws Exception {
        TodoSection section = mock(TodoSection.class);
        given(section.getId()).willReturn(1L);
        given(section.getTitle()).willReturn("Title");

        given(sectionService.update(anyLong(), any(TodoSectionRequest.class)))
                .willReturn(Optional.of(section));

        String body = "{\"title\": \"Title\"}";
        mvc.perform(put(API_SECTIONS + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andDo(print());

        verify(sectionService).update(anyLong(), any(TodoSectionRequest.class));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updateSection_ShouldReturnNotFound() throws Exception {
        given(sectionService.update(anyLong(), any(TodoSectionRequest.class)))
                .willReturn(Optional.empty());

        String body = "{\"title\": \"Title\"}";
        mvc.perform(put(API_SECTIONS + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(sectionService).update(anyLong(), any(TodoSectionRequest.class));
    }

    @Test
    @WithUserDetails(USER)
    public void updateSection_ShouldReturnBadRequest() throws Exception {
        mvc.perform(put(API_SECTIONS + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(sectionService);
    }

    @Test
    @WithUserDetails(USER)
    public void addOrRemoveTasks_ShouldReturnOk() throws Exception {
        doNothing().when(tasksFacade).removeTasks(anyLong(), anySet());

        Map<String, Integer[]> body = new WeakHashMap<>();
        body.put("tasks", new Integer[]{1, 2});
        mvc.perform(post(API_SECTIONS + "1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("do", "move")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithUserDetails(USER)
    public void addOrRemoveTasks_EmptyBody_ShouldReturnBadRequest() throws Exception {
        doNothing().when(tasksFacade).addTasks(anyLong(), anySet());

        mvc.perform(post(API_SECTIONS + "1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("do", "move"))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(tasksFacade);
    }

    @Test
    @WithUserDetails(USER)
    public void addOrRemoveTasks_NoParams_ShouldReturnBadRequest() throws Exception {
        doNothing().when(tasksFacade).addTasks(anyLong(), anySet());

        Map<String, Integer[]> body = new WeakHashMap<>();
        body.put("tasks", new Integer[]{1, 2});
        mvc.perform(post(API_SECTIONS + "1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(tasksFacade);
    }

}

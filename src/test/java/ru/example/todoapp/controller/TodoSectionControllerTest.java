package ru.example.todoapp.controller;
/*
 * Date: 3/12/21
 * Time: 10:30 PM
 * */

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todoapp.controller.request.TodoSectionRequest;
import ru.example.todoapp.repository.projection.TodoSectionProjection;
import ru.example.todoapp.dto.TodoSectionDto;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.TasksFacade;
import ru.example.todoapp.service.TodoSectionService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        TodoSectionProjection section1 = new TodoSectionProjection(1L, "section1", new Date(), new Date());
        TodoSectionProjection section2 = new TodoSectionProjection(2L, "section2", new Date(), new Date());

        given(sectionService.findSections())
                .willReturn(List.of(section1, section2));

        mvc.perform(get(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections[0].title", is("section1")))
                .andExpect(jsonPath("_embedded.sections[1].title", is("section2")))
                .andDo(print());

        verify(sectionService, times(1)).findSections();
    }

    @Ignore
    @Test
    @WithUserDetails(ADMIN)
    public void getSections_ShouldReturnEmptyList() throws Exception {
        given(sectionService.findSections())
                .willReturn(Collections.emptyList());

        mvc.perform(get(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections.isEmpty()", is(true)))
                .andDo(print());

        verify(sectionService, times(1)).findSections();
    }

    // get section by ID
    @Test
    @WithUserDetails(ADMIN)
    public void getSection_ShouldReturnSectionById() throws Exception {
        given(sectionService.findSectionById(1L))
                .willReturn(new TodoSection("Important"));

        mvc.perform(get(API_SECTIONS + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Important")))
                .andDo(print());

        verify(sectionService, times(1)).findSectionById(1L);
    }

    // get section by non-existent id: returns 404 NOT FOUND
    @Test
    @WithUserDetails(USER)
    public void getSection_ShouldReturnBadRequest() throws Exception {
        given(sectionService.findSectionById(1L))
                .willThrow(CustomException.notFound("Section not found: " + 1));

        mvc.perform(get(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("section not found")));

        verify(sectionService, times(1)).findSectionById(1L);
    }

    // delete section
    @Test
    @WithUserDetails(ADMIN)
    public void deleteSection_ShouldDeleteSectionById() throws Exception {
        doNothing().when(sectionService).deleteSectionById(anyLong());

        // delete by id: returns 204 NO CONTENT
        mvc.perform(delete(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(sectionService, times(1)).deleteSectionById(anyLong());
    }

    // delete section by non-existent id: returns 204 NO CONTENT
    @Test
    @WithUserDetails(USER)
    public void deleteSection_ShouldReturnNotFound() throws Exception {
        doThrow(CustomException.notFound("Section not found"))
                .when(sectionService).deleteSectionById(anyLong());

        mvc.perform(delete(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", containsStringIgnoringCase("not found")))
                .andDo(print());

        verify(sectionService, times(1)).deleteSectionById(anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteSection_ShouldReturnForbidden() throws Exception {
        doThrow(CustomException.forbidden("Not enough permissions"))
                .when(sectionService).deleteSectionById(anyLong());

        mvc.perform(delete(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error", containsStringIgnoringCase("Forbidden")))
                .andDo(print());

        verify(sectionService, times(1)).deleteSectionById(anyLong());
    }

    // create new section
    @Test
    @WithUserDetails(ADMIN)
    public void createSection_ShouldReturnStatusCreated() throws Exception {
        given(sectionService.createSection(any(TodoSectionRequest.class)))
                .willReturn(new TodoSection(1L, "title"));

        mvc.perform(post(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoSectionDto("Created Section"))))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(sectionService, times(1)).createSection(any(TodoSectionRequest.class));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void createSection_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(sectionService, times(0)).createSection(any(TodoSectionRequest.class));
    }

    // update section
    @Test
    @WithUserDetails(ADMIN)
    public void updateSection_ShouldReturnOk() throws Exception {
        TodoSection section = mock(TodoSection.class);
        given(section.getId()).willReturn(1L);
        given(section.getTitle()).willReturn("Title");

        given(sectionService.updateSection(anyLong(), any(TodoSectionRequest.class)))
                .willReturn(section);

        // update section by id: returns 200 OK
        mvc.perform(put(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoSectionDto("Title"))))
                .andExpect(status().isOk())
                .andDo(print());

        verify(sectionService, times(1))
                .updateSection(anyLong(), any(TodoSectionRequest.class));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void updateSection_ShouldReturnNotFound() throws Exception {
        doThrow(CustomException.notFound("Section Not Found"))
                .when(sectionService)
                .updateSection(anyLong(), any(TodoSectionRequest.class));

        mvc.perform(put(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoSectionDto("Title"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", containsStringIgnoringCase("not found")))
                .andDo(print());

        verify(sectionService, times(1))
                .updateSection(anyLong(), any(TodoSectionRequest.class));
    }

    @Test
    @WithUserDetails(USER)
    public void updateSection_ShouldReturnForbidden() throws Exception {
        doThrow(CustomException.forbidden("Not enough permissions"))
                .when(sectionService)
                .updateSection(anyLong(), any(TodoSectionRequest.class));

        mvc.perform(put(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoSectionDto("Title"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error", containsStringIgnoringCase("forbidden")))
                .andDo(print());

        verify(sectionService, times(1))
                .updateSection(anyLong(), any(TodoSectionRequest.class));
    }

    @Test
    @WithUserDetails(USER)
    public void updateSection_ShouldReturnBadRequest() throws Exception {
        mvc.perform(put(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(sectionService, times(0))
                .updateSection(anyLong(), any(TodoSectionRequest.class));
    }

    @Test
    @WithUserDetails(USER)
    public void addOrRemoveTasks_ShouldReturnOk() throws Exception {
        doNothing().when(tasksFacade)
                .addTasksToOrRemoveFromSection(anyLong(), anySet(), any());

        Map<String, Integer[]> body = new WeakHashMap<>();
        body.put("tasks", new Integer[]{1, 2});

        mvc.perform(post(API_SECTIONS + "1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "move")
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(tasksFacade, times(1))
                .addTasksToOrRemoveFromSection(anyLong(), anySet(), any());
    }

    @Test
    @WithUserDetails(USER)
    public void addOrRemoveTasks_EmptyBody_ShouldReturnBadRequest() throws Exception {
        doNothing().when(tasksFacade)
                .addTasksToOrRemoveFromSection(anyLong(), anySet(), any());

        mvc.perform(post(API_SECTIONS + "1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "move"))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(tasksFacade, times(0))
                .addTasksToOrRemoveFromSection(anyLong(), anySet(), any());
    }

    @Test
    @WithUserDetails(USER)
    public void addOrRemoveTasks_NoParam_ShouldReturnBadRequest() throws Exception {
        doNothing().when(tasksFacade)
                .addTasksToOrRemoveFromSection(anyLong(), anySet(), any());

        Map<String, Integer[]> body = new WeakHashMap<>();
        body.put("tasks", new Integer[]{1, 2});

        mvc.perform(post(API_SECTIONS + "1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(tasksFacade, times(0))
                .addTasksToOrRemoveFromSection(anyLong(), anySet(), any());
    }

}

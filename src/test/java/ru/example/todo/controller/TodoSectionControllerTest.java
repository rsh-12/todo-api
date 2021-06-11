package ru.example.todo.controller;
/*
 * Date: 3/12/21
 * Time: 10:30 PM
 * */

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.example.todo.controller.wrapper.TaskIdsWrapper;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;
import ru.example.todo.messaging.requests.EmailRequest;
import ru.example.todo.service.TodoSectionService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TodoSectionControllerTest extends AbstractControllerTestClass {

    @MockBean
    private TodoSectionService sectionService;

    // get all sections
    @Test
    @WithUserDetails(ADMIN)
    public void getSections_ShouldReturnListOfSections() throws Exception {
        given(sectionService.findSectionDtoList(Mockito.any(User.class)))
                .willReturn(List.of(new TodoSectionDto("task1"), new TodoSectionDto("task2")));

        mvc.perform(get(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections[0].title", is("task1")))
                .andExpect(jsonPath("_embedded.sections[1].title", is("task2")))
                .andDo(print());

        verify(sectionService, times(1)).findSectionDtoList(Mockito.any(User.class));
    }

    @Ignore
    @Test
    @WithUserDetails(ADMIN)
    public void getSections_ShouldReturnEmptyList() throws Exception {
        given(sectionService.findSectionDtoList(Mockito.any(User.class)))
                .willReturn(Collections.emptyList());

        mvc.perform(get(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.sections.isEmpty()", is(true)))
                .andDo(print());

        verify(sectionService, times(1)).findSectionDtoList(Mockito.any(User.class));
    }

    // get section by ID
    @Test
    @WithUserDetails(ADMIN)
    public void getSection_ShouldReturnSectionById() throws Exception {
        given(sectionService.findSectionById(Mockito.any(User.class), Mockito.anyLong()))
                .willReturn(new TodoSection("Important"));

        mvc.perform(get(API_SECTIONS + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Important")))
                .andDo(print());

        verify(sectionService, times(1)).findSectionById(Mockito.any(User.class), Mockito.anyLong());
    }

    // get section by non-existent id: returns 404 NOT FOUND
    @Test
    @WithUserDetails(USER)
    public void getSection_ShouldReturnBadRequest() throws Exception {
        given(sectionService.findSectionById(Mockito.any(User.class), Mockito.anyLong()))
                .willThrow(new CustomException("Not Found", "Section not found: " + 1, HttpStatus.NOT_FOUND));

        mvc.perform(get(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("section not found")));

        verify(sectionService, times(1)).findSectionById(Mockito.any(User.class), Mockito.anyLong());
    }

    // delete section
    @Test
    @WithUserDetails(ADMIN)
    public void deleteSection_ShouldDeleteSectionById() throws Exception {
        doNothing().when(sectionService).deleteSectionById(Mockito.any(User.class), Mockito.anyLong());

        // delete by id: returns 204 NO CONTENT
        mvc.perform(delete(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(sectionService, times(1)).deleteSectionById(Mockito.any(User.class), Mockito.anyLong());
    }

    // delete section by non-existent id: returns 204 NO CONTENT
    @Test
    @WithUserDetails(USER)
    public void deleteSection_ShouldReturnNotFound() throws Exception {
        doThrow(new CustomException("Not Found", "Section not found", HttpStatus.NOT_FOUND))
                .when(sectionService).deleteSectionById(Mockito.any(User.class), Mockito.anyLong());

        mvc.perform(delete(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", containsStringIgnoringCase("not found")))
                .andDo(print());

        verify(sectionService, times(1)).deleteSectionById(Mockito.any(User.class), Mockito.anyLong());
    }

    @Test
    @WithUserDetails(USER)
    public void deleteSection_ShouldReturnForbidden() throws Exception {
        doThrow(new CustomException("Forbidden", "Not enough permissions", HttpStatus.FORBIDDEN))
                .when(sectionService).deleteSectionById(Mockito.any(User.class), Mockito.anyLong());

        mvc.perform(delete(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error", containsStringIgnoringCase("Forbidden")))
                .andDo(print());

        verify(sectionService, times(1)).deleteSectionById(Mockito.any(User.class), Mockito.anyLong());
    }

    // create new section
    @Test
    @WithUserDetails(ADMIN)
    public void createSection_ShouldReturnStatusCreated() throws Exception {
        doNothing().when(sectionService).createSection(Mockito.any(User.class), Mockito.any(TodoSectionDto.class));

        mvc.perform(post(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(new TodoSectionDto("Created Section"))))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(sectionService, times(1)).createSection(Mockito.any(User.class), Mockito.any(TodoSectionDto.class));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void createSection_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post(API_SECTIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(sectionService, times(0)).createSection(Mockito.any(User.class), Mockito.any(TodoSectionDto.class));
    }

    // update section
    @Test
    @WithUserDetails(ADMIN)
    public void updateSection_ShouldReturnOk() throws Exception {
        doNothing().when(sectionService)
                .updateSection(Mockito.any(User.class), Mockito.anyLong(), Mockito.any(TodoSectionDto.class));

        // update section by id: returns 200 OK
        mvc.perform(put(API_SECTIONS + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(new TodoSectionDto("Title"))))
                .andExpect(status().isOk())
                .andDo(print());

        verify(sectionService, times(1))
                .updateSection(Mockito.any(User.class), Mockito.anyLong(), Mockito.any(TodoSectionDto.class));
    }

    private String getSectionInJson(Long id, String title) {
        return String.format("{\"id\":%d, \"title\":\"%s\"}", id, title);
    }


    @Test
    @WithUserDetails(USER)
    public void updateSection_SectionDoesNotExist_ShouldReturnNotFound() throws Exception {
        final int SECTION_ID = 100;

        mvc.perform(put(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson((long) SECTION_ID, "New title")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Section not found")));
    }

    @Test
    public void updateSection_InvalidTitle_ShouldReturnBadRequest() throws Exception {
        final int SECTION_ID = 3;

        mvc.perform(put(API_SECTIONS + SECTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSectionInJson((long) SECTION_ID, "T")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("title", containsInAnyOrder("Size must be between 3 and 50")));
    }

    // add tasks to the section
    @Test
    @WithUserDetails(ADMIN)
    public void addOrRemoveTasks_AddTasks_ShouldReturnStatusOk() throws Exception {
        final int SECTION_ID = 3;

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>() {{
            addAll(Set.of(4L, 6L));
        }};

        int beforeTasksQuantity = getJsonArraySize(API_SECTIONS + SECTION_ID, "tasks");

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "move")
                .content(convertToJson(wrapper)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(status().isOk());

        int afterTasksQuantity = getJsonArraySize(API_SECTIONS + SECTION_ID, "tasks");

        assertEquals(beforeTasksQuantity + wrapper.tasks.size(), afterTasksQuantity);
    }

    // remove task(s) from the section
    @Test
    @WithUserDetails(ADMIN)
    public void addOrRemoveTasks_RemoveTasks_ShouldReturnStatusOk() throws Exception {

        final int TASK_ID = 10, SECTION_ID = 4;

        int beforeTasksQuantity = getJsonArraySize(API_SECTIONS + SECTION_ID, "tasks");
        System.out.println("beforeTasksQuantity = " + beforeTasksQuantity);

        assertEquals(1, beforeTasksQuantity);

        mvc.perform(get(API_SECTIONS + SECTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("tasks[0].title", containsStringIgnoringCase("special")));

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>() {{
            add((long) TASK_ID);
        }};

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "remove")
                .content(convertToJson(wrapper)))
                .andExpect(status().isOk());

        int afterTasksQuantity = getJsonArraySize(API_SECTIONS + SECTION_ID, "tasks");

        assertEquals(beforeTasksQuantity - wrapper.tasks.size(), afterTasksQuantity);
    }

    @Test
    @WithUserDetails(ADMIN)
    public void addOrRemoveTasks_EmptyIdSet_ShouldReturnBadRequest() throws Exception {
        final int SECTION_ID = 3;

        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>();

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "move")
                .content(convertToJson(wrapper)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Tasks IDs are required")));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void addOrRemoveTasks_Null_ShouldReturnBadRequest() throws Exception {
        final int SECTION_ID = 3;

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = null;

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "remove")
                .content(convertToJson(wrapper)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Tasks IDs are required")));
    }

    @Test
    @WithUserDetails(ADMIN)
    public void addOrRemove_NotFound_ShouldReturnBadRequest() throws Exception {
        final int SECTION_ID = 100;

        // request body
        TaskIdsWrapper wrapper = new TaskIdsWrapper();
        wrapper.tasks = new HashSet<>() {{
            add(4L);
        }};

        mvc.perform(post(API_SECTIONS + SECTION_ID + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("do", "remove")
                .content(convertToJson(wrapper)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message", containsStringIgnoringCase("Section not found")));
    }
}

package ru.example.todoapp.service;
/*
 * Date: 19.09.2021
 * Time: 7:16 PM
 * */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.domain.Role;
import ru.example.todoapp.domain.request.TodoSectionRequest;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoSectionRepository;
import ru.example.todoapp.repository.projection.TodoSectionProjection;
import ru.example.todoapp.service.impl.TodoSectionServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
public class TodoSectionServiceTest {

    @InjectMocks
    private TodoSectionServiceImpl sectionService;

    @Mock
    private TodoSectionRepository sectionRepository;

    @Mock
    private AuthUserFacade authUserFacade;

    // findSectionById
    @Test
    public void findSectionById_ShouldReturnEmpty() {
        given(authUserFacade.getUserId()).willReturn(1L);
        given(sectionRepository.findByUserIdAndId(anyLong(), anyLong())).willReturn(Optional.empty());
        assertTrue(sectionService.findSectionById(1L).isEmpty());
    }

    @Test
    public void findSectionById_ShouldReturnSection() {
        TodoSection section = mock(TodoSection.class);
        given(section.getTitle()).willReturn("Title");

        given(authUserFacade.getUserId()).willReturn(1L);
        given(sectionRepository.findByUserIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.of(section));

        TodoSection todoSection = sectionService.findSectionById(1L).orElse(null);
        assertNotNull(todoSection);
        assertEquals("Title", todoSection.getTitle());
    }

    // findSections
    @Test
    public void findSections_ShouldReturnEmptyPage() {
        given(authUserFacade.getUserId()).willReturn(1L);
        given(sectionRepository.findAllByUserIdProjection(1L, Pageable.unpaged()))
                .willReturn(Page.empty());

        Page<TodoSectionProjection> sections = sectionService.findSections(Pageable.unpaged());
        assertTrue(sections.isEmpty());
    }

    @Test
    public void findSections_ShouldReturnPage() {
        var mockSection1 = TodoSectionProjection.withCurrentDateTime(1L, "section1");
        var mockSection2 = TodoSectionProjection.withCurrentDateTime(2L, "section2");
        Page<TodoSectionProjection> page = new PageImpl<>(List.of(mockSection1, mockSection2));

        given(authUserFacade.getUserId()).willReturn(1L);
        given(sectionRepository.findAllByUserIdProjection(1L, Pageable.unpaged()))
                .willReturn(page);

        Page<TodoSectionProjection> sections = sectionService.findSections(Pageable.unpaged());
        assertFalse(sections.isEmpty());
        assertEquals(2, sections.getContent().size());
    }

    // deleteSectionById
    @Test
    public void deleteSectionById_SectionNotFound_ShouldThrowException() {
        given(sectionRepository.findById(anyLong())).willReturn(Optional.empty());
        assertThrows(CustomException.class, () -> sectionService.deleteSectionById(1L));
    }

    @Test
    public void deleteSectionById_UserNotFound_ShouldThrowException() {
        TodoSection section = mock(TodoSection.class);
        given(sectionRepository.findById(anyLong())).willReturn(Optional.of(section));
        given(section.getUser()).willReturn(null);
        assertThrows(CustomException.class, () -> sectionService.deleteSectionById(1L));
    }

    @Test
    public void deleteSectionById_AccessDenied_ShouldThrowException() {
        TodoSection section = mock(TodoSection.class);
        User user = mock(User.class);
        User candidate = mock(User.class);

        given(user.getId()).willReturn(1L);
        given(user.getRoles()).willReturn(Collections.singleton(Role.USER));
        given(section.getUser()).willReturn(user);

        given(candidate.getId()).willReturn(2L);
        given(authUserFacade.getLoggedUser()).willReturn(candidate);
        given(sectionRepository.findById(anyLong())).willReturn(Optional.of(section));

        assertThrows(CustomException.class, () -> sectionService.deleteSectionById(1L));
    }

    @Test
    public void deleteSectionById() {
        TodoSection section = mock(TodoSection.class);

        User user = new User();
        user.setId(1L);
        user.setRoles(Collections.singleton(Role.USER));

        User loggedUser = new User();
        loggedUser.setId(1L);
        loggedUser.setRoles(Collections.singleton(Role.USER));

        given(sectionRepository.findById(anyLong())).willReturn(Optional.of(section));
        given(section.getUser()).willReturn(user);
        given(authUserFacade.getLoggedUser()).willReturn(loggedUser);
        doNothing().when(sectionRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> sectionService.deleteSectionById(1L));
    }

    // createSection
    @Test
    public void createSection_ShouldReturnSavedSection() {
        given(authUserFacade.getLoggedUser()).willReturn(mock(User.class));
        given(sectionRepository.save(any(TodoSection.class))).willReturn(mock(TodoSection.class));
        assertDoesNotThrow(() -> sectionService.createSection(new TodoSectionRequest("important")));
    }

    // updateSection
    @Test
    public void updateSection_SectionNotFound_ShouldReturnEmpty() {
        given(sectionRepository.findById(anyLong())).willReturn(Optional.empty());
        assertEquals(Optional.empty(), sectionService.updateSection(1L, new TodoSectionRequest("Title")));
    }

    @Test
    public void updateSection_UserNotFound_ShouldReturnEmpty() {
        given(sectionRepository.findById(anyLong())).willReturn(Optional.of(mock(TodoSection.class)));
        assertEquals(Optional.empty(), sectionService.updateSection(1L, new TodoSectionRequest("Title")));
    }

    @Test
    public void updateSection_AccessDenied_ShouldReturnEmpty() {
        User user = new User();
        user.setId(1L);
        user.setRoles(Collections.singleton(Role.USER));

        User loggedUser = new User();
        loggedUser.setId(2L);
        loggedUser.setRoles(Collections.singleton(Role.USER));

        TodoSection section = new TodoSection();
        section.setUser(user);

        given(authUserFacade.getLoggedUser()).willReturn(loggedUser);
        given(sectionRepository.findById(anyLong())).willReturn(Optional.of(section));
        given(sectionRepository.save(any(TodoSection.class))).willReturn(section);

        Optional<TodoSection> sectionOptional = sectionService.updateSection(1L, new TodoSectionRequest("Title"));
        assertEquals(Optional.empty(), sectionOptional);
    }

}

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
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoSectionRepository;
import ru.example.todoapp.repository.projection.TodoSectionProjection;
import ru.example.todoapp.service.impl.TodoSectionServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
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

}

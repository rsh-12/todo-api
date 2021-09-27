package ru.example.todoapp.service;
/*
 * Date: 19.09.2021
 * Time: 7:16 PM
 * */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoSectionRepository;
import ru.example.todoapp.service.impl.TodoSectionServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

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

}

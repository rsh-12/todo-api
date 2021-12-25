package ru.example.todoapp.service;
/*
 * Date: 19.09.2021
 * Time: 7:16 PM
 * */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoSectionRepository;
import ru.example.todoapp.service.impl.TodoSectionServiceImpl;

@ExtendWith(SpringExtension.class)
public class TodoSectionServiceTest {

    @InjectMocks
    private TodoSectionServiceImpl sectionService;

    @Mock
    private TodoSectionRepository sectionRepository;

    @Mock
    private AuthUserFacade authUserFacade;

    @Test
    public void addTasks_ShouldDoNothing() {
        var task = new TodoTask("Title");
        assertNull(task.getTodoSection());

        given(sectionRepository.findByUserIdAndId(anyLong(), anyLong())).willReturn(
                Optional.empty());
        sectionService.addTasks(1L, 1L, List.of(task));

        assertNull(task.getTodoSection());
    }

    @Test
    public void addTasks_ShouldMoveTasks() {
        var section = new TodoSection("Important");
        var task = new TodoTask("Title");
        assertNull(task.getTodoSection());

        given(sectionRepository.findByUserIdAndId(anyLong(), anyLong())).willReturn(
                Optional.of(section));
        sectionService.addTasks(1L, 1L, List.of(task));

        assertNotNull(task.getTodoSection());
        assertEquals(section.getTitle(), task.getTodoSection().getTitle());
    }

    @Test
    public void removeTasks_ShouldRemoveTasks() {
        var task = new TodoTask("Title");
        TodoSection section = new TodoSection().addTodoTask(task);

        assertEquals("Title", section.getTodoTasks().get(0).getTitle());
        assertNotNull(task.getTodoSection());

        given(sectionRepository.findByUserIdAndId(anyLong(), anyLong())).willReturn(
                Optional.of(section));
        sectionService.removeTasks(1L, 1L, List.of(task));

        assertNull(task.getTodoSection());
    }

}

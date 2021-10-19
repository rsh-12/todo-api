package ru.example.todoapp.facade;
/*
 * Date: 7/2/21
 * Time: 3:20 AM
 * */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.impl.TasksFacadeImpl;
import ru.example.todoapp.service.TodoSectionService;
import ru.example.todoapp.service.TodoTaskService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(SpringExtension.class)
public class TasksFacadeTest {

    @InjectMocks
    private TasksFacadeImpl tasksFacade;

    @Mock
    private TodoTaskService taskService;

    @Mock
    private AuthUserFacade authUserFacade;

    @Mock
    private TodoSectionService sectionService;

    @Test
    public void addTasks_ShouldCallServiceMethods() {
        TodoTask task1 = mock(TodoTask.class);
        TodoTask task2 = mock(TodoTask.class);

        given(authUserFacade.getUserId()).willReturn(1L);
        given(taskService.findByIds(anySet(), anyLong())).willReturn(List.of(task1, task2));
        doNothing().when(sectionService).addTasks(anyLong(), anyLong(), anyList());

        tasksFacade.addTasks(1L, Set.of(1L, 2L));

        verify(taskService).findByIds(anySet(), anyLong());
        verify(sectionService).addTasks(anyLong(), anyLong(), anyList());
    }

    @Test
    public void addTasks_ShouldThrowCustomException() {
        assertThrows(CustomException.class, () -> tasksFacade.addTasks(1L, Collections.emptySet()));

        verifyNoInteractions(taskService);
        verifyNoInteractions(sectionService);
    }
}

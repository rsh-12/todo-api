package ru.example.todo.facade;
/*
 * Date: 7/2/21
 * Time: 3:20 AM
 * */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.filters.FilterByOperation;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.TodoSectionService;
import ru.example.todo.service.TodoTaskService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TasksFacadeTest {

    @Autowired
    private TasksFacade tasksFacade;

    @MockBean
    private TodoTaskService taskService;

    @MockBean
    private TodoSectionService sectionService;

    @Test
    public void addTasksToOrRemoveFromSection_ShouldCallServiceMethods() {
        TodoTask task1 = mock(TodoTask.class);
        TodoTask task2 = mock(TodoTask.class);

        given(taskService.findTasksByIds(anySet(), anyLong())).willReturn(List.of(task1, task2));
        doNothing().when(sectionService).addTasksToOrRemoveFromSection(anyLong(), anyLong(), anyList(), any());

        tasksFacade.addTasksToOrRemoveFromSection(1L, 1L, Set.of(1L, 2L), FilterByOperation.MOVE);

        verify(taskService).findTasksByIds(anySet(), anyLong());
        verify(sectionService).addTasksToOrRemoveFromSection(anyLong(), anyLong(), anyList(), any());
    }

    @Test
    public void addTasksToOrRemoveFromSection_ShouldThrowCustomException() {
        assertThrows(CustomException.class, () -> tasksFacade
                .addTasksToOrRemoveFromSection(1L, 1L, Collections.emptySet(), FilterByOperation.MOVE));

        verifyNoInteractions(taskService);
        verifyNoInteractions(sectionService);
    }
}

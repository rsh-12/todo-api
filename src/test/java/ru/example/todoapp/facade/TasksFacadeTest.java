package ru.example.todoapp.facade;
/*
 * Date: 7/2/21
 * Time: 3:20 AM
 * */

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.exception.BadRequestException;
import ru.example.todoapp.facade.impl.TasksFacadeImpl;
import ru.example.todoapp.service.TodoSectionService;
import ru.example.todoapp.service.TodoTaskService;

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
    public void addTasks_ShouldThrowCustomException() {
        assertThrows(BadRequestException.class, () -> tasksFacade.addTasks(1L, Collections.emptySet()));

        verifyNoInteractions(taskService);
        verifyNoInteractions(sectionService);
    }
}


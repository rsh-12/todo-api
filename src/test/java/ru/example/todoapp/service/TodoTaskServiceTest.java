package ru.example.todoapp.service;
/*
 * Date: 6/19/21
 * Time: 10:21 AM
 * */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.domain.request.TodoTaskRequest;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoTaskRepository;
import ru.example.todoapp.service.impl.TodoTaskServiceImpl;
import ru.example.todoapp.util.filters.FilterByDate;

@ExtendWith(SpringExtension.class)
public class TodoTaskServiceTest {

    @InjectMocks
    private TodoTaskServiceImpl taskService;

    @Mock
    private TodoTaskRepository taskRepository;

    @Mock
    private AuthUserFacade authUserFacade;

    @Test
    public void findTasks_ShouldReturnTodaysTasks() {
        TodoTask task = mock(TodoTask.class);
        given(task.getTitle()).willReturn("task");

        given(taskRepository.findAllByCompletionDateEqualsAndUserId(any(), anyLong(), any()))
                .willReturn(new PageImpl<>(List.of(task)));

        List<TodoTask> tasks = taskService.findAll(FilterByDate.TODAY, Pageable.unpaged())
                .getContent();

        assertFalse(tasks.isEmpty());
        assertEquals("task", tasks.get(0).getTitle());
    }

    @Test
    public void findTasks_ShouldReturnOverdueTasks() {
        TodoTask task = mock(TodoTask.class);
        given(task.getTitle()).willReturn("task");

        given(taskRepository.findAllByCompletionDateBeforeAndUserId(any(), anyLong(), any()))
                .willReturn(new PageImpl<>(List.of(task)));

        List<TodoTask> tasks = taskService.findAll(FilterByDate.OVERDUE, Pageable.unpaged())
                .getContent();

        assertFalse(tasks.isEmpty());
        assertEquals("task", tasks.get(0).getTitle());
    }

    // findTaskById
    @Test
    public void findTaskById_ShouldReturnTask() {
        TodoTask task = mock(TodoTask.class);

        given(task.getTitle()).willReturn("Title");
        given(taskRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(task));

        TodoTask taskFromDb = taskService.findOne(1L).orElse(null);

        assertNotNull(taskFromDb);
        assertEquals("Title", taskFromDb.getTitle());
    }

    // findTasksByIds
    @Test
    public void findTasksByIds_ShouldReturnListOfTasks() {
        TodoTask task1 = mock(TodoTask.class);
        given(task1.getTitle()).willReturn("Task1");

        TodoTask task2 = mock(TodoTask.class);
        given(task2.getTitle()).willReturn("Task2");

        given(taskRepository.findAllByIdInAndUserId(anySet(), anyLong())).willReturn(
                List.of(task1, task2));
        List<TodoTask> tasks = taskService.findByIds(Set.of(1L, 2L), 1L);

        assertNotNull(tasks);
        assertEquals(2, tasks.size());

        assertTrue(tasks.get(0).getTitle().startsWith("Task"));
        assertTrue(tasks.get(1).getTitle().startsWith("Task"));
    }

    // saveTask
    @Test
    public void saveTask_ShouldSaveTask() {
        TodoTask task = new TodoTask("Task");
        given(taskRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(task));
        given(taskRepository.save(any(TodoTask.class))).willReturn(task);

        assertNotEquals("New title", task.getTitle());
        assertFalse(task.isStarred());

        TodoTaskRequest request = new TodoTaskRequest("New title", LocalDate.now(), true);
        task = taskService.update(1L, request).orElse(null);

        assertNotNull(task);
        assertEquals("New title", task.getTitle());
        assertTrue(task.isStarred());
    }

    @Test
    public void saveTask_ShouldReturnEmpty() {
        given(taskRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());
        TodoTaskRequest request = new TodoTaskRequest("New title", LocalDate.now(), true);

        assertEquals(Optional.empty(), taskService.update(1L, request));
    }

}

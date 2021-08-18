package ru.example.todoapp.service;
/*
 * Date: 6/19/21
 * Time: 10:21 AM
 * */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.controller.request.TodoTaskRequest;
import ru.example.todoapp.dto.TodoTaskDto;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.enums.filters.FilterByDate;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoTaskRepository;
import ru.example.todoapp.service.impl.TodoTaskServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(SpringExtension.class)
public class TodoTaskServiceTest {

    @InjectMocks
    private TodoTaskServiceImpl taskService;

    @Mock
    private TodoTaskRepository taskRepository;

    @Mock
    private AuthUserFacade authUserFacade;

    // findTasks
    /*@Test
    public void findTasks_ShouldReturnAllUserTasks() {
        TodoTask task1 = mock(TodoTask.class);
        given(task1.getTitle()).willReturn("task1");

        TodoTask task2 = mock(TodoTask.class);
        given(task2.getTitle()).willReturn("task2");

        given(taskRepository.findAllByUserId(anyLong(), any(Pageable.class))).willReturn(List.of(task1, task2));

        List<TodoTask> tasks = taskService.findTasks(0, 10, FilterByDate.ALL, "secId");
        assertFalse(tasks.isEmpty());
        assertEquals(2, tasks.size());

        assertTrue(tasks.get(0).getTitle().startsWith("task"));
        assertTrue(tasks.get(1).getTitle().startsWith("task"));

        verify(taskRepository, times(1)).findAllByUserId(anyLong(), any(Pageable.class));
    }*/

    /*@Test
    public void findTasks_ShouldReturnTodaysTasks() {
        TodoTask task = mock(TodoTask.class);
        given(task.getTitle()).willReturn("task");

        given(taskRepository.findAllByCompletionDateEqualsAndUserId(any(), any(), anyLong()))
                .willReturn(List.of(task));

        List<TodoTask> tasks = taskService.findTasks(0, 1000, FilterByDate.TODAY, "secId,asc");
        assertFalse(tasks.isEmpty());
        assertEquals("task", tasks.get(0).getTitle());
    }*/

    /*@Test
    public void findTasks_ShouldReturnOverdueTasks() {
        TodoTask task = mock(TodoTask.class);
        given(task.getTitle()).willReturn("task");

        given(taskRepository.findAllByCompletionDateBeforeAndUserId(any(), any(), anyLong()))
                .willReturn(List.of(task));

        List<TodoTask> tasks = taskService.findTasks(0, 1000, FilterByDate.OVERDUE, "secId");
        assertFalse(tasks.isEmpty());
        assertEquals("task", tasks.get(0).getTitle());
    }*/

    // findTaskById
    @Test
    public void findTaskById_ShouldReturnTask() {
        TodoTask task = mock(TodoTask.class);
        given(task.getTitle()).willReturn("Title");

        given(taskRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(task));

        TodoTask taskFromDb = taskService.findTaskById(1L);

        assertNotNull(task);
        assertEquals("Title", taskFromDb.getTitle());

        verify(taskRepository).findByIdAndUserId(anyLong(), anyLong());
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    public void findTaskById_ShouldThrowException() {
        given(taskRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());
        given(authUserFacade.getUserId()).willReturn(1L);
        assertThrows(CustomException.class, () -> taskService.findTaskById(1L));
    }

    // deleteTaskById
    @Test
    public void deleteTaskById_ShouldDeleteTaskById() {
        User user = mock(User.class);
        TodoTask task = mock(TodoTask.class);

        given(task.getId()).willReturn(1L);
        given(task.getUser()).willReturn(user);
        given(authUserFacade.getLoggedUser()).willReturn(user);
        given(taskRepository.findById(anyLong())).willReturn(Optional.of(task));

        doNothing().when(taskRepository).deleteById(anyLong());
        taskService.deleteTaskById(task.getId());

        verify(taskRepository).deleteById(anyLong());
    }

    @Test
    public void deleteTaskById_ShouldThrowCustomException() {
        given(taskRepository.findById(anyLong())).willReturn(Optional.empty());
        assertThrows(CustomException.class, () -> taskService.deleteTaskById(1L));
    }


    @Test
    public void createTask_ShouldReturnTask() {
        TodoTaskRequest request = new TodoTaskRequest("Task", LocalDate.now(), true);

        User user = mock(User.class);
        TodoTask task = mock(TodoTask.class);

        given(authUserFacade.getLoggedUser()).willReturn(user);
        given(taskRepository.save(any(TodoTask.class))).willReturn(task);

        assertNotNull(taskService.createTask(request));
    }

    // findTasksByIds
    @Test
    public void findTasksByIds_ShouldReturnListOfTasks() {
        TodoTask task1 = mock(TodoTask.class);
        given(task1.getTitle()).willReturn("Task1");

        TodoTask task2 = mock(TodoTask.class);
        given(task2.getTitle()).willReturn("Task2");

        given(taskRepository.findAllByIdInAndUserId(anySet(), anyLong())).willReturn(List.of(task1, task2));
        List<TodoTask> tasks = taskService.findTasksByIds(Set.of(1L, 2L), 1L);

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
        task = taskService.saveTask(1L, request);

        assertNotNull(task);
        assertEquals("New title", task.getTitle());
        assertTrue(task.isStarred());
    }

    @Test
    public void saveTask_ShouldThrowCustomException() {
        given(taskRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());

        TodoTaskRequest request = new TodoTaskRequest("New title", LocalDate.now(), true);
        assertThrows(CustomException.class, () -> taskService.saveTask(1L, request));
    }

    @Test
    public void mapToTaskDto() {
        TodoTask task = new TodoTask("task", LocalDate.now());
        TodoTaskDto taskDto = taskService.mapToTaskDto(task);
        assertNotNull(taskDto);
    }

}

package ru.example.todo.service;
/*
 * Date: 6/19/21
 * Time: 10:21 AM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoTaskRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class TodoTaskServiceTest extends AbstractServiceTestClass {

    @Autowired
    private TodoTaskService taskService;

    @MockBean
    private TodoTaskRepository taskRepository;

    //  findTasks

    //  findTaskById
    @Test
    public void findTaskById_ShouldReturnTask() {
        given(taskRepository.findByIdAndUserId(anyLong(), anyLong()))
                .willReturn(java.util.Optional.of(new TodoTask("Title")));
        TodoTask task = taskService.findTaskById(1L, 1L);

        assertNotNull(task);
        assertEquals("Title", task.getTitle());

        verify(taskRepository).findByIdAndUserId(anyLong(), anyLong());
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    public void findTaskById_ShouldThrowException() {
        given(taskRepository.findByIdAndUserId(anyLong(), anyLong()))
                .willThrow(new CustomException("Not Found", "Task not found", HttpStatus.NOT_FOUND));
        assertThrows(CustomException.class, () -> taskService.findTaskById(1L, 1L));
    }

    //  deleteTaskById
    @Test
    public void deleteTaskById_ShouldThrowCustomException_NotFound() {
        given(taskRepository.findById(anyLong()))
                .willThrow(new CustomException("Not Found", "Task not found", HttpStatus.NOT_FOUND));

        assertThrows(CustomException.class, () -> taskService
                .deleteTaskById(new User(1L, "name", Collections.singleton(Role.USER)), 1L));

        verify(taskRepository).findById(anyLong());
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    public void deleteTaskById_ShouldThrowCustomException_Forbidden() {
        User principal = mock(User.class);
        given(principal.getRoles()).willReturn(Collections.singleton(Role.USER));

        User taskUser = mock(User.class);
        TodoTask task = mock(TodoTask.class);
        given(task.getUser()).willReturn(taskUser);

        given(taskRepository.findById(anyLong())).willReturn(Optional.of(task));

        assertThrows(CustomException.class, () -> taskService.deleteTaskById(principal, 1L));

        verify(taskRepository).findById(anyLong());
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    public void deleteTaskById_ShouldDeleteTaskById() {
        User principal = mock(User.class);
        given(principal.getId()).willReturn(1L);
        given(principal.getRoles()).willReturn(Set.of(Role.ADMIN, Role.USER));

        TodoTask task = mock(TodoTask.class);
        given(task.getUser()).willReturn(principal);
        given(task.getId()).willReturn(1L);

        given(taskRepository.findById(anyLong())).willReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById(anyLong());

        taskService.deleteTaskById(principal, task.getId());

        verify(taskRepository).findById(anyLong());
        verify(taskRepository).deleteById(anyLong());
    }

    //  createTask
    @Test
    public void createTask_ShouldReturnTask() {
        TodoTask task = new TodoTask("task");
        User user = new User("name", "pwd");

        given(taskRepository.save(any())).willReturn(task);
        TodoTask createdTask = taskService.createTask(user, task);

        assertNotNull(createdTask);
        assertEquals(createdTask.getUser(), user);
    }

    //  updateTask

    //  findTasksByIds
    @Test
    public void findTasksByIds_ShouldReturnListOfTasks() {
        given(taskRepository.findAllByIdInAndUserId(anySet(), anyLong()))
                .willReturn(List.of(new TodoTask("Task1"), new TodoTask("Task2")));
        List<TodoTask> tasks = taskService.findTasksByIds(Set.of(1L, 2L), 1L);

        assertNotNull(tasks);
        assertEquals(2, tasks.size());

        assertTrue(tasks.get(0).getTitle().startsWith("Task"));
        assertTrue(tasks.get(1).getTitle().startsWith("Task"));
    }

}

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
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoTaskRepository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
    //  createTask
    //  updateTask
    //  findTasksByIds

}

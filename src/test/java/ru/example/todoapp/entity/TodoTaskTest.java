package ru.example.todoapp.entity;
/*
 * Date: 04.07.2021
 * Time: 9:03 PM
 * */

import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import ru.example.todoapp.dto.TodoTaskDto;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TodoTaskTest {

    private final ModelMapper modelMapper = new ModelMapper();

    @Before
    public void setUp() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
    }

    @Test
    public void mapTodoTask_WithTitleAndCompletionDate() {
        TodoTaskDto todoTaskDto = new TodoTaskDto("title", LocalDate.now());
        TodoTask todoTask = modelMapper.map(todoTaskDto, TodoTask.class);

        assertNotNull(todoTask);
        assertEquals("title", todoTask.getTitle());
    }

    @Test
    public void mapTodoTask_WithTitle() {
        TodoTask task = new TodoTask();
        task.setTitle("title");

        TodoTaskDto todoTaskDto = new TodoTaskDto();
        todoTaskDto.setCompletionDate(LocalDate.now());

        modelMapper.map(todoTaskDto, task);

        assertNotNull(task);
        assertEquals("title", task.getTitle());
    }

}

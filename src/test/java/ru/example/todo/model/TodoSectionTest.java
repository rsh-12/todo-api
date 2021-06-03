package ru.example.todo.model;
/*
 * Date: 6/3/21
 * Time: 9:59 AM
 * */

import org.junit.Before;
import org.junit.Test;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class TodoSectionTest {

    private TodoSection section;
    private final TodoTask task = new TodoTask("First task", LocalDate.of(2021, 5, 5));

    @Before
    public void setUp() throws Exception {
        section = new TodoSection("Important");
        section.addTask(task);
        assertEquals(1, section.getTodoTasks().size());
    }

    @Test
    public void addTask_ShouldAddTaskToSection() {
        section.addTask(new TodoTask("Make a call"));
        assertEquals(2, section.getTodoTasks().size());
    }

    @Test
    public void removeTask_ShouldRemoveTask() {
        section.removeTask(task);
        assertTrue(section.getTodoTasks().isEmpty());
    }

    @Test
    public void addTask_ShouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> section.addTask(null));
    }

    @Test
    public void removeTask_ShouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> section.removeTask(null));
    }

}

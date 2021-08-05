package ru.example.todoapp.entity;
/*
 * Date: 6/3/21
 * Time: 9:59 AM
 * */

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TodoSectionTest {

    private TodoSection section;
    private final TodoTask task = new TodoTask("First task", LocalDate.of(2021, 5, 5));

    @Before
    public void setUp() {
        section = new TodoSection("Important");
        section.addTask(task);
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

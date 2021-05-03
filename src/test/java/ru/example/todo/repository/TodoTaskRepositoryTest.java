package ru.example.todo.repository;
/*
 * Date: 3/15/21
 * Time: 9:45 AM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.exception.CustomException;

import java.util.List;

import static org.junit.Assert.*;


public class TodoTaskRepositoryTest extends AbstractRepositoryClass {

    @Autowired
    private TodoTaskRepository repository;

    // get all
    @Test
    public void getAllTasks() {
        List<TodoTask> tasks = repository.findAll();
        assertEquals(7, tasks.size());
    }

    // get by id
    @Test
    public void testGetTaskById() {
        final Long TASK_ID = 1L;

        assertTrue(repository.existsById(TASK_ID));

        TodoTask task = repository.findById(TASK_ID).orElse(null);
        assertNotNull(task);

        assertEquals(TASK_ID, task.getId());
        assertEquals("Read a book", task.getTitle());
    }

    // create new task
    @Test
    public void testCreateTask() {
        TodoTask task = new TodoTask();
        task.setTitle("New task title");

        int beforeTasksQuantity = repository.findAll().size();

        entityManager.persistAndFlush(task);

        int afterTasksQuantity = repository.findAll().size();

        assertEquals(beforeTasksQuantity + 1, afterTasksQuantity);

        TodoTask taskFromDB = repository.findById(task.getId()).orElse(null);
        assertNotNull(taskFromDB);

        assertEquals(task.getTitle(), taskFromDB.getTitle());
    }

    // update by id
    @Test
    public void testUpdateTask() {
        final Long TASK_ID = 1L;
        final String newTitle = "Take a walk";

        assertTrue(repository.existsById(TASK_ID));

        TodoTask task = repository.findById(TASK_ID).orElse(null);
        assertNotNull(task);

        assertEquals("Read a book", task.getTitle());

        task.setTitle(newTitle);
        entityManager.persistAndFlush(task);

        TodoTask updatedTask = repository.findById(TASK_ID).orElse(null);
        assertNotNull(updatedTask);

        assertEquals(newTitle, updatedTask.getTitle());
    }

    // delete by id
    @Test
    public void testDeleteTask() {
        final Long TASK_ID = 2L;

        assertTrue(repository.existsById(TASK_ID));

        int beforeTasksQuantity = repository.findAll().size();

        repository.deleteById(TASK_ID);
        entityManager.flush();

        assertFalse(repository.existsById(TASK_ID));

        int afterTasksQuantity = repository.findAll().size();

        assertEquals(beforeTasksQuantity - 1, afterTasksQuantity);
    }

    // delete all
    @Test
    public void testDeleteAllTasks() {

        int beforeTasksQuantity = repository.findAll().size();
        assertTrue(beforeTasksQuantity != 0);

        repository.deleteAll();
        entityManager.flush();

        int aftterTasksQuantity = repository.findAll().size();
        assertEquals(0, aftterTasksQuantity);
    }
}

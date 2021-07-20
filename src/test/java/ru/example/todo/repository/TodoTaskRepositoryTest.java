package ru.example.todo.repository;
/*
 * Date: 3/15/21
 * Time: 9:45 AM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.example.todo.entity.TodoTask;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


public class TodoTaskRepositoryTest extends AbstractRepositoryTestClass {

    @Autowired
    private TodoTaskRepository repository;

    // get all
    @Test
    public void findAll_ShouldReturnListOfTasks() {
        List<TodoTask> tasks = repository.findAll();
        assertEquals(7, tasks.size());
    }

    @Test
    public void findAllByCompletionDateEqualsAndUserId() {
        Pageable page = PageRequest.of(0, 10, Sort.by("id"));
        List<TodoTask> tasks = repository.findAllByCompletionDateEqualsAndUserId(LocalDate.now(), page, 1L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long count = tasks.stream().filter(task -> {
            String currentDate = simpleDateFormat.format(new Date());
            return task.getCompletionDate().toString().equals(currentDate);
        }).count();

        assertEquals(tasks.size(), count);
    }

    // get by id
    @Test
    public void findById_ShouldReturnTaskById() {
        final Long TASK_ID = 1L;

        assertTrue(repository.existsById(TASK_ID));

        TodoTask task = repository.findById(TASK_ID).orElse(null);
        assertNotNull(task);

        assertEquals(TASK_ID, task.getId());
        assertEquals("Read a book", task.getTitle());
    }

    // create new task
    @Test
    public void createTask_ShouldCreateTask() {
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
    public void updateTask_ShouldUpdateTaskById() {
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
    public void deleteById_ShouldDeleteTaskById() {
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
    public void deleteAll_ShouldDeleteAllTasks() {

        int beforeTasksQuantity = repository.findAll().size();
        assertTrue(beforeTasksQuantity != 0);

        repository.deleteAll();
        entityManager.flush();

        int aftterTasksQuantity = repository.findAll().size();
        assertEquals(0, aftterTasksQuantity);
    }
}

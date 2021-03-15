package ru.example.todo.repository;
/*
 * Date: 3/15/21
 * Time: 9:45 AM
 * */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.exception.TodoObjectException;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TodoTaskTest {

    @Autowired
    private TodoTaskRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    // get all
    @Test
    public void getAllTasks() {
        List<TodoTask> tasks = repository.findAll();
        assertEquals(6, tasks.size());
    }


    // create one
    // update by id
    // delete by id
    // z_delete all*
}

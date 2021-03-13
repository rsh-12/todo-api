package ru.example.todo.repository;
/*
 * Date: 3/13/21
 * Time: 7:24 PM
 * */

import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.exception.TodoObjectException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TodoSectionTest {

    @Autowired
    private TodoSectionRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testGetSectionById() {
        TodoSection section = repository.findById(1L).orElseThrow(() -> new TodoObjectException("Not found"));
        // There are already 4 sections in the database, and one them is "Important"
        assertEquals(section.getTitle(), "Important");
    }

    @Test
    public void testCreateNewSection() {
        TodoSection section = new TodoSection("Tomorrow");
        entityManager.persist(section);
        entityManager.flush();

        TodoSection sectionFromDB = repository.findById(section.getId())
                .orElseThrow(() -> new TodoObjectException("Not found"));

        assert sectionFromDB != null;
        assertEquals(section.getTitle(), sectionFromDB.getTitle());
    }
}

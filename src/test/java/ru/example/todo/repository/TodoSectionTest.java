package ru.example.todo.repository;
/*
 * Date: 3/13/21
 * Time: 7:24 PM
 * */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.exception.TodoObjectException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TodoSectionTest {

    @Autowired
    private TodoSectionRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testGetAllSections() {
        List<TodoSection> sections = repository.findAll();
        assertEquals(3, sections.size());
    }

    @Test
    public void testGetSectionById() {
        TodoSection section = repository.findById(1L).orElseThrow(() -> new TodoObjectException("Not found"));
        assertEquals(section.getTitle(), "Important");
    }

    @Test
    public void testCreateNewSection() {
        TodoSection section = new TodoSection("Tomorrow");

        Date date = new Date();
        section.setCreatedAt(new Timestamp(date.getTime()));
        section.setUpdatedAt(new Timestamp(date.getTime()));

        entityManager.persist(section);
        entityManager.flush();

        TodoSection sectionFromDB = repository.findById(section.getId())
                .orElseThrow(() -> new TodoObjectException("Not found"));

        assert sectionFromDB != null;
        assertEquals(section.getTitle(), sectionFromDB.getTitle());
    }

    @Test
    public void testDeleteAllSections() {
        repository.deleteAll();
        entityManager.flush();

        List<TodoSection> sections = repository.findAll();
        assertEquals(0, sections.size());
    }
}

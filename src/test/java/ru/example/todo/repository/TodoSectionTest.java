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
import ru.example.todo.exception.CustomException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

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
        TodoSection section = repository.findById(1L).orElseThrow(() -> new CustomException("Not found"));
        assertEquals(section.getTitle(), "Important");
    }

    @Test
    public void testCreateNewSection() {
        TodoSection section = new TodoSection("Tomorrow");

        Date date = new Date();
        section.setCreatedAt(new Timestamp(date.getTime()));
        section.setUpdatedAt(new Timestamp(date.getTime()));

        entityManager.persistAndFlush(section);

        TodoSection sectionFromDB = repository.findById(section.getId())
                .orElseThrow(() -> new CustomException("Not found"));

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

    @Test
    public void testDeleteSectionById() {
        final Long SECTION_ID = 3L;

        assertTrue(repository.existsById(SECTION_ID));

        repository.deleteById(SECTION_ID);
        entityManager.flush();

        assertFalse(repository.existsById(SECTION_ID));
    }

    @Test
    public void testUpdateSectionById() {
        TodoSection section = repository.findById(2L)
                .orElseThrow(() -> new CustomException("Section not found"));

        assertNotEquals("New title", section.getTitle());

        section.setTitle("New title");
        Date date = new Date();
        section.setUpdatedAt(new Timestamp(date.getTime()));
        entityManager.persistAndFlush(section);

        TodoSection updatedSection = repository.findById(2L)
                .orElseThrow(() -> new CustomException("Section not found"));

        assertEquals("New title", updatedSection.getTitle());
    }
}

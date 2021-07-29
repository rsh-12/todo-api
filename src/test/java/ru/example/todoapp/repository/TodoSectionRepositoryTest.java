package ru.example.todoapp.repository;
/*
 * Date: 3/13/21
 * Time: 7:24 PM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.todoapp.entity.TodoSection;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class TodoSectionRepositoryTest extends AbstractRepositoryTestClass{

    @Autowired
    private TodoSectionRepository repository;

    @Test
    public void deleteByIdAndUserId_ShouldDeleteSectionByIdAndUserId() {
        TodoSection beforeDeleting = repository.findByUserIdAndId(ADMIN_ID, SECTIONS[0]).orElse(null);
        assertNotNull(beforeDeleting);

        repository.deleteByIdAndUserId(SECTIONS[0], ADMIN_ID);
        entityManager.flush();

        TodoSection afterDeleting = repository.findByUserIdAndId(ADMIN_ID, SECTIONS[0]).orElse(null);
        assertNull(afterDeleting);
    }

    @Test
    public void findByUserIdAndId_ShouldReturnSectionByIdAndByUserId() {
        TodoSection s1 = repository.findByUserIdAndId(ADMIN_ID, SECTIONS[0]).orElse(null);
        TodoSection s2 = repository.findByUserIdAndId(ADMIN_ID, SECTIONS[2]).orElse(null);
        TodoSection s3 = repository.findByUserIdAndId(ADMIN_ID, SECTIONS[1]).orElse(null);

        assertNotNull(s1);
        assertEquals("Important", s1.getTitle());

        assertNotNull(s2);
        assertEquals("Later", s2.getTitle());

        assertNull(s3);
    }

    @Test
    public void findAll_ShouldReturnListOfSections() {
        List<TodoSection> sections = repository.findAll();
        assertEquals(5, sections.size());
    }

    @Test
    public void findById_ShouldReturnSectionById() {
        TodoSection section = repository.findById(1L).orElse(null);
        assertNotNull(section);
        assertEquals(section.getTitle(), "Important");
    }

    @Test
    public void createSection_ShouldCreateSection() {
        TodoSection section = new TodoSection("Tomorrow");

        Date date = new Date();
        section.setCreatedAt(new Timestamp(date.getTime()));
        section.setUpdatedAt(new Timestamp(date.getTime()));

        entityManager.persistAndFlush(section);

        TodoSection sectionFromDB = repository.findById(section.getId()).orElse(null);
        assertNotNull(sectionFromDB);

        assert sectionFromDB != null;
        assertEquals(section.getTitle(), sectionFromDB.getTitle());
    }

    @Test
    public void deleteAll_ShouldDeleteAllSections() {
        repository.deleteAll();
        entityManager.flush();

        List<TodoSection> sections = repository.findAll();
        assertEquals(0, sections.size());
    }

    @Test
    public void deleteById_ShouldDeleteSectionById() {
        final Long SECTION_ID = 3L;

        assertTrue(repository.existsById(SECTION_ID));

        repository.deleteById(SECTION_ID);
        entityManager.flush();

        assertFalse(repository.existsById(SECTION_ID));
    }

    @Test
    public void updateSection_ShouldUpdateSection() {
        TodoSection section = repository.findById(2L).orElse(null);
        assertNotNull(section);

        assertNotEquals("New title", section.getTitle());

        section.setTitle("New title");
        Date date = new Date();
        section.setUpdatedAt(new Timestamp(date.getTime()));
        entityManager.persistAndFlush(section);

        TodoSection updatedSection = repository.findById(2L).orElse(null);

        assertEquals("New title", updatedSection.getTitle());
    }
}

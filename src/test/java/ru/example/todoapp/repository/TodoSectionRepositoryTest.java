package ru.example.todoapp.repository;
/*
 * Date: 24.12.2021
 * Time: 6:31 AM
 * */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.example.todoapp.PostgreSqlTestBase;
import ru.example.todoapp.entity.TodoSection;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TodoSectionRepositoryTest extends PostgreSqlTestBase {

    @Autowired
    private TodoSectionRepository repository;

    private TodoSection savedEntity;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        TodoSection entity = new TodoSection("Importnant");
        savedEntity = repository.save(entity);

        assertEquals(entity.getId(), savedEntity.getId());
        assertEquals(entity.getTitle(), savedEntity.getTitle());
        assertEquals(entity.getCreatedAt(), savedEntity.getCreatedAt());
    }

    @Test
    public void create() {
        TodoSection newEntity = new TodoSection("Education");
        repository.save(newEntity);

        TodoSection foundEntity = repository.findById(newEntity.getId()).orElse(null);
        assertNotNull(foundEntity);

        assertEqualsTodoSection(newEntity, foundEntity);
        assertEquals(2, repository.count());
    }

    private void assertEqualsTodoSection(TodoSection expectedEntity, TodoSection actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getTitle(), actualEntity.getTitle());
        assertEquals(expectedEntity, actualEntity);
    }

}

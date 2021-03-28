package ru.example.todo.repository;
/*
 * Date: 3/26/21
 * Time: 12:36 PM
 * */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todo.entity.User;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// todo update tests
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    private final static String ADMIN_USERNAME = "admin@mail.com";
    private final static String CLIENT_USERNAME = "client@mail.com";

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testGetAllSections() {
        List<User> users = repository.findAll();
        assertEquals(2, users.size());
    }

    @Test
    public void testFindByUserName() {
        User admin = repository.findByUsername(ADMIN_USERNAME)
                .orElse(null);

        User client = repository.findByUsername(CLIENT_USERNAME)
                .orElse(null);

        assertNotNull(admin);
        assertEquals(ADMIN_USERNAME, admin.getUsername());

        assertNotNull(client);
        assertEquals(CLIENT_USERNAME, client.getUsername());
    }


//    boolean existsByUsername(String username);

}

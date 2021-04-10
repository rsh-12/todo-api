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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;

import java.util.List;

import static org.junit.Assert.*;

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
    public void testGetAllUsers() {
        List<User> users = repository.findAll();
        assertEquals(4, users.size());
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

    @Test
    public void testExistsByUsername() {
        assertTrue(repository.existsByUsername(ADMIN_USERNAME));
        assertTrue(repository.existsByUsername(CLIENT_USERNAME));
        assertFalse(repository.existsByUsername("john@mail.com"));
    }

    // create user
    @Test
    public void testCreateUser() {
        User user = createUser("harry@mail.com");

        assertFalse(repository.existsByUsername(user.getUsername()));
        entityManager.persistAndFlush(user);
        assertTrue(repository.existsByUsername(user.getUsername()));
    }

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("secretpassword12345");
        return user;
    }

    private User createAndSaveUser() {
        User user = createUser("ola@mail.com");
        return entityManager.persistAndFlush(user);
    }

    // update user
    @Test
    public void testUpdateUser() {
        String username = "ola@mail.com";
        String newUsername = "newOla@mail.com";
        createAndSaveUser();

        assertTrue(repository.existsByUsername(username));
        User user = repository.findByUsername(username).orElse(null);
        assertNotNull(user);

        user.setUsername(newUsername);
        entityManager.persistAndFlush(user);

        User updatedUser = repository.findByUsername(newUsername).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUsername());
    }

    // delete user
    @Test
    public void testDeleteUser() {
        String username = "client2@mail.com";

        assertTrue(repository.existsByUsername(username));

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        repository.delete(user);
        entityManager.flush();

        assertFalse(repository.existsByUsername(username));
    }
}

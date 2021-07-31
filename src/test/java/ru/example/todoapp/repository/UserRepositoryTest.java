package ru.example.todoapp.repository;
/*
 * Date: 3/26/21
 * Time: 12:36 PM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.todoapp.entity.User;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


public class UserRepositoryTest extends AbstractRepositoryTestClass {

    @Autowired
    private UserRepository repository;

    @Test
    public void findAll_ShouldReturnListOfUsers() {
        List<User> users = repository.findAll();
        assertEquals(4, users.size());
    }

    @Test
    public void findByUsername_ShouldReturnUsersByUsername() {
        User admin = repository.findByUsername(ADMIN_USERNAME)
                .orElse(null);

        User client = repository.findByUsername(CLIENT_USERNAME)
                .orElse(null);

        assertNotNull(admin);
        assertEquals(ADMIN_USERNAME, admin.getUsername());

        assertNotNull(client);
        assertEquals(CLIENT_USERNAME, client.getUsername());

        assertNotNull(admin.getCreatedAt());
        System.out.println("admin = " + admin);
    }

    @Test
    public void existsByUsername_ShouldReturnCorrectBoolean() {
        assertTrue(repository.existsByUsername(ADMIN_USERNAME));
        assertTrue(repository.existsByUsername(CLIENT_USERNAME));
        assertFalse(repository.existsByUsername("john@mail.com"));
    }

    // create user
    @Test
    public void createUser_ShouldCreateUser() {
        User user = createAndGetUser("harry@mail.com");

        assertFalse(repository.existsByUsername(user.getUsername()));

        entityManager.persistAndFlush(user);

        assertTrue(repository.existsByUsername(user.getUsername()));
    }


    // update user
    @Test
    public void updateUser_ShouldUpdateUsername() {
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
    public void delete_ShouldDeleteUser() {
        String username = "client2@mail.com";

        assertTrue(repository.existsByUsername(username));

        User user = repository.findByUsername(username).orElse(null);
        assertNotNull(user);

        repository.delete(user);
        entityManager.flush();

        assertFalse(repository.existsByUsername(username));
    }

    @Test
    public void createUser_UsernameAlreadyTaken_ShouldThrowException() {
        User user = createAndGetUser("admin@mail.com");
        assertThrows(Exception.class, () -> entityManager.persist(user));
    }
}

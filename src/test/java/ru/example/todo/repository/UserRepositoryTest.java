package ru.example.todo.repository;
/*
 * Date: 3/26/21
 * Time: 12:36 PM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


public class UserRepositoryTest extends AbstractRepositoryTestClass {

    @Autowired
    private UserRepository repository;

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
        user.setRoles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN));

        assertFalse(repository.existsByUsername(user.getUsername()));
        entityManager.persistAndFlush(user);
        assertTrue(repository.existsByUsername(user.getUsername()));
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

        User user = repository.findByUsername(username).orElse(null);
        assertNotNull(user);

        repository.delete(user);
        entityManager.flush();

        assertFalse(repository.existsByUsername(username));
    }
}

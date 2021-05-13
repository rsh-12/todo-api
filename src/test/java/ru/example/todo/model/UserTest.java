package ru.example.todo.model;
/*
 * Date: 5/13/21
 * Time: 7:40 AM
 * */

import org.junit.Test;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {

    @Test
    public void createUser_ShouldContainRoleUser() {
        User user = new User();
        assertTrue(user.getRoles().contains(Role.ROLE_USER));
        assertFalse(user.getRoles().contains(Role.ROLE_ADMIN));
    }
}

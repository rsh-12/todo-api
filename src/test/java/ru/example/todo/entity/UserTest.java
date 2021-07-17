package ru.example.todo.entity;
/*
 * Date: 5/13/21
 * Time: 7:40 AM
 * */

import org.junit.Test;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {

    @Test
    public void createUser_ShouldContainRoleUser() {
        User user = new User();
        assertTrue(user.getRoles().contains(Role.USER));
        assertFalse(user.getRoles().contains(Role.ADMIN));
    }

    @Test
    public void setRoles_ShouldContainAllRoles() {
        Set<Role> roles = Set.of(Role.USER, Role.ADMIN);
        User user = new User();
        user.setRoles(roles);
        assertTrue(user.getRoles().containsAll(roles));
    }

    @Test
    public void clearRoles_ShouldClearRoles() {
        User user = new User();
        assertTrue(user.getRoles().contains(Role.USER));

        user.clearRoles();
        assertFalse(user.getRoles().stream()
                .anyMatch(role -> role.equals(Role.USER) || role.equals(Role.ADMIN)));
    }
}

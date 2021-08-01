package ru.example.todoapp.entity;
/*
 * Date: 5/13/21
 * Time: 7:40 AM
 * */

import org.junit.Before;
import org.junit.Test;
import ru.example.todoapp.enums.Role;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setRoles(Collections.singleton(Role.USER));
        user.setUsername("username");
        user.setPassword("password");
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void createUser_ShouldContainRoleUser() {
        assertTrue(user.getRoles().contains(Role.USER));
        assertFalse(user.getRoles().contains(Role.ADMIN));
    }

    @Test
    public void setRoles_ShouldContainAllRoles() {
        Set<Role> roles = Set.of(Role.USER, Role.ADMIN);
        user.setRoles(roles);
        assertTrue(user.getRoles().containsAll(roles));
    }

    @Test
    public void clearRoles_ShouldClearRoles() {
        assertTrue(user.getRoles().contains(Role.USER));
        user.clearRoles();

        assertFalse(user.getRoles().stream()
                .anyMatch(role -> role.equals(Role.USER) || role.equals(Role.ADMIN)));
    }

    @Test
    public void equals_ShouldBeEqual() {
        User user2 = new User();
        user2.setId(1L);
        user2.setRoles(Collections.singleton(Role.USER));
        user2.setUsername("username");
        assertEquals(user, user2);
    }

    @Test
    public void equals_ShouldNotBeEqual() {
        User user2 = new User();
        user2.setId(2L);
        user2.setRoles(Collections.singleton(Role.USER));
        assertNotEquals(user, user2);
    }

    @Test
    public void equals_Symmetric_ShouldBeEqual() {
        User user2 = new User();
        user2.setId(1L);
        user2.setRoles(Collections.singleton(Role.USER));
        user2.setUsername("username");
        assertTrue(user.equals(user2) && user2.equals(user));
        assertEquals(user.hashCode(), user2.hashCode());
    }

}

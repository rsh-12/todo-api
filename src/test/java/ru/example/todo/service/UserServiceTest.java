package ru.example.todo.service;
/*
 * Date: 5/10/21
 * Time: 7:06 PM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.config.UserServiceImplTestConfig;

import static org.junit.Assert.*;


@Import(UserServiceImplTestConfig.class)
public class UserServiceTest extends AbstractServiceTestClass {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void register_ShouldThrowCustomException() {
        User user = createAndGetUser("admin@mail.com", null);
        assertThrows(CustomException.class, () -> userService.register(user));
    }

    @Test
    public void getUserByUsername_ShouldReturnUser() {
        User user = userService.findUserByUsername("admin@mail.com");
        assertNotNull(user);
        assertTrue(user.getRoles().contains(Role.ADMIN));
    }

    @Test
    public void getUserByUsername_ShouldThrowCustomException() {
        assertThrows(CustomException.class, () -> userService.findUserByUsername("notexist@mail.com"));
    }

    @Test
    public void getUserById_ShouldReturnUser() {
        User user = userService.findUserById(1L);
        assertNotNull(user);
        assertEquals("admin@mail.com", user.getUsername());
    }

    @Test
    public void getUserById_ShouldThrowCustomException() {
        assertThrows(CustomException.class, () -> userService.findUserById(0L));
    }

    @Test
    public void updatePassword_ShouldThrowCustomException_400() {
        User user = createAndGetUser("some@mail.com", null);
        assertThrows(CustomException.class, () -> userService.updatePassword(user.getUsername(), null));
        assertThrows(CustomException.class, () -> userService.updatePassword(user.getUsername(), ""));
        assertThrows(CustomException.class, () -> userService.updatePassword(user.getUsername(), "1234567"));
        assertThrows(CustomException.class, () -> userService.updatePassword(user.getUsername(), "                "));
    }

    @Test
    public void updatePassword_ShouldUpdatePassword() throws InterruptedException {
        User user = createAndGetUser("some@mail.com", null);
        String newPassword = "newPassword";
        userService.updatePassword(user.getUsername(), newPassword);

        Thread.sleep(200);
        assertTrue(bCryptPasswordEncoder.matches(newPassword, user.getPassword()));
    }
}

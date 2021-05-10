package ru.example.todo.service;
/*
 * Date: 5/10/21
 * Time: 7:06 PM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.example.todo.entity.User;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.config.UserServiceImplTestConfig;

import java.util.Collections;

import static org.junit.Assert.assertThrows;


@Import(UserServiceImplTestConfig.class)
public class UserServiceTest extends AbstractServiceTestClass {

    @Autowired
    private UserService userService;

    @Test
    public void register_ShouldThrowCustomException() {
        User user = createUser("admin@mail.com");
        assertThrows(CustomException.class, () -> userService.register(user));
    }

    public User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("somepassword");
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        return user;
    }
}

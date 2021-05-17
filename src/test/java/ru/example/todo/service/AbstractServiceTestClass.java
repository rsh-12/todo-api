package ru.example.todo.service;
/*
 * Date: 5/9/21
 * Time: 9:38 AM
 * */

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todo.entity.User;

import java.util.Date;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbstractServiceTestClass {

    // for userservice test
    User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setCreatedAt(new Date());

        user.setPassword(Objects.requireNonNullElse(password, "somepassword"));

        return user;
    }
}

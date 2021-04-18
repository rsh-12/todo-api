package ru.example.todo.repository;
/*
 * Date: 4/17/21
 * Time: 9:17 PM
 * */

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todo.entity.User;

@RunWith(SpringRunner.class)
@DataJpaTest
abstract class AbstractRepositoryClass {

    final static String ADMIN_USERNAME = "admin@mail.com";
    final static String CLIENT_USERNAME = "client@mail.com";

    static final Long ADMIN_ID = 1L;
    static final Long USER_ID = 2L;
    static final Long[] SECTIONS = new Long[]{1L, 2L, 3L};

    @Autowired
    TestEntityManager entityManager;

    User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("secretpassword12345");
        return user;
    }

    User createAndSaveUser() {
        User user = createUser("ola@mail.com");
        return entityManager.persistAndFlush(user);
    }

}

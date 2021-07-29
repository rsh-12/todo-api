package ru.example.todoapp.repository;
/*
 * Date: 4/17/21
 * Time: 9:17 PM
 * */

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.example.todoapp.entity.User;

@RunWith(SpringRunner.class)
@DataJpaTest
abstract class AbstractRepositoryTestClass {

    final static String ADMIN_USERNAME = "admin@mail.com";
    final static String CLIENT_USERNAME = "client@mail.com";

    static final Long ADMIN_ID = 1L;
    static final Long USER_ID = 2L;
    static final Long[] SECTIONS = new Long[]{1L, 2L, 3L};

    @Autowired
    TestEntityManager entityManager;

    User createAndGetUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("secretpassword12345");
        return user;
    }

    User createAndSaveUser() {
        User user = createAndGetUser("ola@mail.com");
        return entityManager.persistAndFlush(user);
    }

}

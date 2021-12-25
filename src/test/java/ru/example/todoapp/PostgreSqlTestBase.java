package ru.example.todoapp;
/*
 * Date: 24.12.2021
 * Time: 6:26 AM
 * */

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgreSqlTestBase {

    private static final PostgreSQLContainer container =
            new PostgreSQLContainer("postgres:14");

    static {
        container.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

}

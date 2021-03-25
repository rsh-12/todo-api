package ru.example.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.example.todo.entity.Role;
import ru.example.todo.entity.User;
import ru.example.todo.repository.UserRepository;

import java.util.Collections;

@SpringBootApplication
public class TodoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (!userRepository.existsByUsername("admin@mail.com")) {
            User admin = new User();
            admin.setUsername("admin@mail.com");
            admin.setPassword(bCryptPasswordEncoder.encode("admin"));
            admin.setRoles(Collections.singleton(Role.ROLE_ADMIN));
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("client@mail.com")) {
            User client = new User();
            client.setUsername("client@mail.com");
            client.setPassword(bCryptPasswordEncoder.encode("client"));
            client.setRoles(Collections.singleton(Role.ROLE_USER));

            userRepository.save(client);
        }

    }
}

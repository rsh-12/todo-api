package ru.example.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todoapp.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}

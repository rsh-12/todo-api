package ru.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todo.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByValue(String value);

    Optional<RefreshToken> findByUserId(Long userId);

}

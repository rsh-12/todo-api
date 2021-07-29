package ru.example.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todoapp.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByValue(String value);

    Optional<RefreshToken> findByUserId(Long userId);

}

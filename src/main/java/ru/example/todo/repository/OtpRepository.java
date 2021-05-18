package ru.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todo.entity.Otp;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByUsername(String email);

}

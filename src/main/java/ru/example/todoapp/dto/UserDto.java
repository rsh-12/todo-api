package ru.example.todoapp.dto;

import java.time.LocalDateTime;

public record UserDto(String username, LocalDateTime createdAt) {

}

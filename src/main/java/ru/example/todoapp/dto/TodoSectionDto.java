package ru.example.todoapp.dto;
/*
 * Date: 3/12/21
 * Time: 5:46 PM
 * */

import java.time.LocalDateTime;

public record TodoSectionDto(String title, LocalDateTime updatedAt, LocalDateTime createdAt) {

}

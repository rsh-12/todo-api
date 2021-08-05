package ru.example.todoapp.dto;
/*
 * Date: 3/12/21
 * Time: 5:46 PM
 * */

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TodoSectionDto(String title) {
}

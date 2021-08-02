package ru.example.todoapp.controller.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TodoSectionRequest(
        @NotBlank
        @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
        String title) {
}

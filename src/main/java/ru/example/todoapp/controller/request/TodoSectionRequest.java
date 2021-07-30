package ru.example.todoapp.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record TodoSectionRequest(
        @NotBlank
        @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
        String title) {
}

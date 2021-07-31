package ru.example.todoapp.controller.request;
/*
 * Date: 31.07.2021
 * Time: 10:09 AM
 * */

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public record TodoTaskRequest(
        @NotBlank
        @Size(min = 3, max = 80, message = "Size must be between 3 and 80")
        String title,

        @FutureOrPresent
        LocalDate completionDate,
        boolean completed,
        boolean starred) {
}

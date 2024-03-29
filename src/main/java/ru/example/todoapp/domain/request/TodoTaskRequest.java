package ru.example.todoapp.domain.request;
/*
 * Date: 31.07.2021
 * Time: 10:09 AM
 * */

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TodoTaskRequest(
        @NotBlank
        @Size(min = 3, max = 80, message = "Size must be between 3 and 80")
        String title,

        @FutureOrPresent
        LocalDate completionDate,
        boolean starred) {

    public TodoTaskRequest {
        completionDate = Objects.requireNonNullElse(completionDate, LocalDate.now());
    }

}

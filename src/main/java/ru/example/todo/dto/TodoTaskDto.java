package ru.example.todo.dto;
/*
 * Date: 3/12/21
 * Time: 6:52 PM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class TodoTaskDto {

    @Size(min = 3, max = 80, message = "Size must be between 3 and 80")
    private String title;

    @FutureOrPresent
    private LocalDate completionDate = LocalDate.now();

    public TodoTaskDto() {
    }

    public TodoTaskDto(String title) {
        this.title = title;
    }

    public TodoTaskDto(String title, LocalDate completionDate) {
        this.title = title;
        this.completionDate = completionDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Yekaterinburg")
    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    @Override
    public String toString() {
        return "TodoTaskRequest{" +
                "title='" + title + '\'' +
                ", completionDate=" + completionDate +
                '}';
    }
}

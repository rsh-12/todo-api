package ru.example.todo.domain;
/*
 * Date: 3/12/21
 * Time: 6:52 PM
 * */

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class TodoTaskRequest {

    @Size(min = 3, max = 80, message = "Size must be between 3 and 80")
    private String title;


    @FutureOrPresent
    private LocalDate completionDate = LocalDate.now();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


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

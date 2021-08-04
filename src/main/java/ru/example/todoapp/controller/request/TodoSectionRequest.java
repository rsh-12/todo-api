package ru.example.todoapp.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class TodoSectionRequest {

    @NotBlank
    @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
    private String title;

    public TodoSectionRequest() {
    }

    public TodoSectionRequest(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

}

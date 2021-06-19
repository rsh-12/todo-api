package ru.example.todo.dto;
/*
 * Date: 3/12/21
 * Time: 5:46 PM
 * */

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class TodoSectionDto {

    @NotEmpty
    @NotBlank
    @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
    private String title;


    public TodoSectionDto() {
    }

    public TodoSectionDto(String title) {
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
        return "TodoSectionDto{" +
                "title='" + title + '\'' +
                '}';
    }

}

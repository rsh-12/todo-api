package ru.example.todo.dto;
/*
 * Date: 3/12/21
 * Time: 5:46 PM
 * */

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

public class TodoSectionDto {

    @NotEmpty
    @NotBlank
    @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
    private String title;

    private Date createdAt;

    private Date updatedAt;

    public TodoSectionDto() {
    }

    public TodoSectionDto(String title) {
        this.title = title;
    }

    public TodoSectionDto(String title, Date createdAt, Date updatedAt) {
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TodoSectionDto{" +
                "title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}

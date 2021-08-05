package ru.example.todoapp.repository.projection;
/*
 * Date: 6/19/21
 * Time: 2:10 PM
 * */

import java.time.LocalDateTime;

public record TodoSectionProjection(Long id, String title, LocalDateTime updatedAt, LocalDateTime createdAt) {

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}

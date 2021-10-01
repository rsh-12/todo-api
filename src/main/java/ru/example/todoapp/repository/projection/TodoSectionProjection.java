package ru.example.todoapp.repository.projection;
/*
 * Date: 6/19/21
 * Time: 2:10 PM
 * */

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TodoSectionProjection(Long id, String title, LocalDateTime updatedAt, LocalDateTime createdAt) {

    public static TodoSectionProjection withCurrentDateTime(Long id, String title) {
        return new TodoSectionProjection(id, title, LocalDateTime.now(), LocalDateTime.now());
    }

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

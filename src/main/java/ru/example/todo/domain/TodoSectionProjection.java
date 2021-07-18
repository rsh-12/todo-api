package ru.example.todo.domain;
/*
 * Date: 6/19/21
 * Time: 2:10 PM
 * */

import java.util.Date;

public record TodoSectionProjection(Long id, String title, Date updatedAt, Date createdAt) {

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

}

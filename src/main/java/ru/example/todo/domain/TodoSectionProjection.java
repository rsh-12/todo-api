package ru.example.todo.domain;
/*
 * Date: 6/19/21
 * Time: 2:10 PM
 * */

import java.util.Date;

public class TodoSectionProjection {

    private Long id;
    private String title;
    private Date updatedAt;
    private Date createdAt;

    public TodoSectionProjection(Long id) {
        this.id = id;
    }

    public TodoSectionProjection(String title) {
        this.title = title;
    }

    public TodoSectionProjection(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public TodoSectionProjection(Long id, String title, Date updatedAt, Date createdAt) {
        this.id = id;
        this.title = title;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

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

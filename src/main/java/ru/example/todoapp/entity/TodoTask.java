package ru.example.todoapp.entity;
/*
 * Date: 1/13/21
 * Time: 4:23 PM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "task")
public class TodoTask {

    @Id
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 80, message = "Size must be between 3 and 80")
    private String title;

    @Column(columnDefinition = "boolean default false")
    private boolean completed;

    @Column(columnDefinition = "boolean default false")
    private boolean starred;

    @Column(columnDefinition = "date default current_date")
    private LocalDate completionDate;

    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "list_id")
    private TodoSection todoSection;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public TodoTask() {
    }

    public TodoTask(String title) {
        this.title = title;
    }

    public TodoTask(String title, LocalDate completionDate) {
        this.title = title;
        this.completionDate = completionDate;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = Objects.requireNonNullElseGet(completionDate, LocalDate::now);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TodoSection getTodoSection() {
        return todoSection;
    }

    public void setTodoSection(TodoSection todoSection) {
        this.todoSection = todoSection;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoTask todoTask = (TodoTask) o;

        if (!id.equals(todoTask.id)) return false;
        return Objects.equals(user, todoTask.user);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "TodoTask{" +
                "title='" + title + '\'' +
                ", completed=" + completed +
                ", starred=" + starred +
                '}';
    }

}

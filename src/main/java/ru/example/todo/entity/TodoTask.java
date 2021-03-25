package ru.example.todo.entity;
/*
 * Date: 1/13/21
 * Time: 4:23 PM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.server.core.Relation;
import ru.example.todo.util.Views;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "task")
@Relation(value = "task", collectionRelation = "tasks")
public class TodoTask {

    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 80, message = "Size must be between 3 and 80")
    @JsonView(value = Views.Public.class)
    private String title;

    @JsonView(value = Views.Public.class)
    @Column(columnDefinition = "boolean default false")
    private boolean completed;

    @JsonView(value = Views.Public.class)
    @Column(columnDefinition = "boolean default false")
    private boolean starred;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Yekaterinburg")
    @JsonView(value = Views.Public.class)
    @Column(columnDefinition = "date default current_date")
    private LocalDate completionDate = LocalDate.now();

    // @Column(columnDefinition = "timestamp default current_timestamp", updatable = false)
    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @JsonView(value = Views.Public.class)
    @CreationTimestamp
    private Date createdAt;

    // @Column(columnDefinition = "timestamp default current_timestamp")
    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @JsonView(value = Views.Public.class)
    @UpdateTimestamp
    private Date updatedAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "list_id")
    private TodoSection todoSection;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
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

    public void setId(Long id) {
        this.id = id;
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
        this.completionDate = completionDate;
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
    public String toString() {
        return "TodoTask{" +
                "title='" + title + '\'' +
                ", completed=" + completed +
                ", starred=" + starred +
                '}';
    }

}

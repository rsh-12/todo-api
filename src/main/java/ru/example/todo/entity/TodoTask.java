package ru.example.todo.entity;
/*
 * Date: 1/13/21
 * Time: 4:23 PM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.hateoas.server.core.Relation;
import ru.example.todo.util.Views;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "task")
@Relation(value = "task", collectionRelation = "tasks")
public class TodoTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;

    @Size(min = 3, max = 80, message = "Size must be between 3 and 80")
    @Column(name = "title")
    @JsonView(value = Views.Public.class)
    private String title;

    @Column(name = "completed", columnDefinition = "boolean default false")
    @JsonView(value = Views.Public.class)
    private boolean completed;

    @Column(name = "starred", columnDefinition = "boolean default false")
    @JsonView(value = Views.Public.class)
    private boolean starred;


    @Column(name = "completion_date", columnDefinition = "date default current_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Yekaterinburg")
    @JsonView(value = Views.Public.class)
    private LocalDate completionDate = LocalDate.now();

    @Column(name = "created_at", columnDefinition = "timestamp default current_timestamp", updatable = false)
    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @JsonView(value = Views.Public.class)
    private Date createdAt = new Date();

    @Column(name = "updated_at", columnDefinition = "timestamp default current_timestamp")
    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @JsonView(value = Views.Public.class)
    private Date updatedAt = new Date();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "list_id")
    private TodoSection todoSection;

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

    @Override
    public String toString() {
        return "TodoTask{" +
                "title='" + title + '\'' +
                ", completed=" + completed +
                ", starred=" + starred +
                '}';
    }

}

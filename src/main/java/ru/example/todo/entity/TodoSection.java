package ru.example.todo.entity;
/*
 * Date: 1/13/21
 * Time: 4:25 PM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.server.core.Relation;
import ru.example.todo.util.Views;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "section")
@Relation(value = "section", collectionRelation = "sections")
public class TodoSection {

    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
    @JsonView(value = Views.Public.class)
    private String title;

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

    @OneToMany(mappedBy = "todoSection", cascade = {CascadeType.PERSIST})
    @JsonView(value = Views.Internal.class)
    List<TodoTask> todoTasks;

    @PreRemove
    private void preRemove() {
        todoTasks.forEach(task->task.setTodoSection(null));
    }

    public TodoSection() {
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

    public TodoSection(String title) {
        this.title = title;
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

    @JsonProperty("tasks")
    public List<TodoTask> getTodoTasks() {
        return todoTasks;
    }

    public void setTodoTasks(List<TodoTask> todoTasks) {
        todoTasks.forEach(task -> task.setTodoSection(this));
    }

    public void removeTodoTasks(List<TodoTask> todoTasks) {
        todoTasks.forEach(task -> task.setTodoSection(null));
    }

    @Override
    public String toString() {
        return "TodoSection{" +
                "title='" + title + '\'' +
                '}';
    }
}

package ru.example.todo.entity;
/*
 * Date: 1/13/21
 * Time: 4:25 PM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "section")
@Relation(value = "section", collectionRelation = "sections")
public class TodoSection {

    @Id
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotBlank
    @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
    private String title;

    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @CreationTimestamp
    private Date createdAt;

    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @UpdateTimestamp
    private Date updatedAt;

    @OneToMany(mappedBy = "todoSection", cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    List<TodoTask> todoTasks = new ArrayList<>();

    // @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @PreRemove
    private void preRemove() {
        todoTasks.forEach(task -> task.setTodoSection(null));
    }

    public TodoSection() {
    }

    public TodoSection(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
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

    public void addTask(TodoTask task) {
        if (task == null) throw new NullPointerException("Can't add null Task");
        getTodoTasks().add(task);
        task.setTodoSection(this);
    }

    public void removeTask(TodoTask task) {
        if (task == null) throw new NullPointerException("Can't remove null task");
        getTodoTasks().remove(task);
        task.setTodoSection(null);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "TodoSection{" +
                "title='" + title + '\'' +
                '}';
    }
}

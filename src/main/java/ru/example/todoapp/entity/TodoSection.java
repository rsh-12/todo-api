package ru.example.todoapp.entity;
/*
 * Date: 1/13/21
 * Time: 4:25 PM
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
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "section")
public class TodoSection {

    @Id
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
    private String title;

    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "todoSection", cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    List<TodoTask> todoTasks = new ArrayList<>();

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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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
        if (this.todoTasks != null) {
            this.todoTasks.forEach(task -> task.setTodoSection(null));
        }
        if (todoTasks != null) {
            todoTasks.forEach(task -> task.setTodoSection(this));
        }
        this.todoTasks = todoTasks;
    }

    public void removeTodoTasks(List<TodoTask> todoTasks) {
        if (todoTasks != null) {
            todoTasks.forEach(task -> task.setTodoSection(null));
        }
    }

    public TodoSection addTodoTask(TodoTask task) {
        this.todoTasks.add(task);
        task.setTodoSection(this);
        return this;
    }

    public TodoSection removeTodoTask(TodoTask task) {
        this.todoTasks.remove(task);
        task.setTodoSection(null);
        return this;
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

        TodoSection that = (TodoSection) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(title, that.title)) return false;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TodoSection{" +
                "title='" + title + '\'' +
                '}';
    }
}

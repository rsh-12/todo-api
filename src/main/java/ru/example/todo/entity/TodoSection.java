package ru.example.todo.entity;
/*
 * Date: 1/13/21
 * Time: 4:25 PM
 * */

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "custom_list")
public class TodoSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50, message = "Size must be between 2 and 50")
    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "todoSection")
    List<TodoTask> todoTasks;

    public TodoSection() {
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
        this.title = title;
    }

    public List<TodoTask> getTodoTasks() {
        return todoTasks;
    }

    public void setTodoTasks(List<TodoTask> todoTasks) {
        this.todoTasks = todoTasks;
    }

    @Override
    public String toString() {
        return "TodoSection{" +
                "title='" + title + '\'' +
                '}';
    }
}

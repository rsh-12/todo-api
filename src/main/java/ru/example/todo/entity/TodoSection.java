package ru.example.todo.entity;
/*
 * Date: 1/13/21
 * Time: 4:25 PM
 * */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.hateoas.server.core.Relation;
import ru.example.todo.util.Views;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "custom_list")
@Relation(value = "section", collectionRelation = "sections")
public class TodoSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
    @Column(name = "title")
    @JsonView(value = Views.Public.class)
    private String title;

    @OneToMany(mappedBy = "todoSection")
    @JsonView(value = Views.Internal.class)
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

package ru.example.todo.dto;
/*
 * Date: 3/12/21
 * Time: 5:46 PM
 * */

import ru.example.todo.entity.TodoTask;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class TodoSectionDto {

    @NotBlank
    @Size(min = 3, max = 50, message = "Size must be between 3 and 50")
    private String title;

    public List<TodoTask> todoTasks;

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
        return "TodoSectionDto{" +
                "title='" + title + '\'' +
                ", todoTasks=" + todoTasks +
                '}';
    }
}

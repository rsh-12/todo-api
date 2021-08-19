package ru.example.todoapp.dsl;
/*
 * Date: 18.08.2021
 * Time: 10:44 AM
 * */

import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.entity.User;

import java.time.LocalDate;

public class TaskBuilder {

    private final TodoTask task = new TodoTask();

    private TaskBuilder(String title, LocalDate completionDate) {
        task.setTitle(title);
        task.setCompletionDate(completionDate);
    }

    public static TaskBuilder forTask(String title) {
        return new TaskBuilder(title, LocalDate.now());
    }

    public TaskBuilder user(User user) {
        task.setUser(user);
        return this;
    }

    public TaskBuilder starred(boolean isStarred) {
        task.setStarred(isStarred);
        return this;
    }

    public TaskBuilder completed(boolean isCompleted) {
        task.setCompleted(isCompleted);
        return this;
    }

    public TaskBuilder completionDate(LocalDate completionDate) {
        task.setCompletionDate(completionDate);
        return this;
    }

    public TodoTask build() {
        return task;
    }

}

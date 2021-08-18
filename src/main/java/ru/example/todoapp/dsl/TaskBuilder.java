package ru.example.todoapp.dsl;
/*
 * Date: 18.08.2021
 * Time: 10:44 AM
 * */

import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.entity.User;

import java.time.LocalDate;
import java.util.function.Consumer;

public class TaskBuilder {

    private final TodoTask task = new TodoTask();

    public static TodoTask task(Consumer<TaskBuilder> consumer) {
        TaskBuilder builder = new TaskBuilder();
        consumer.accept(builder);

        return builder.task;
    }

    public void title(String title) {
        task.setTitle(title);
    }

    public void completionDate(LocalDate date) {
        task.setCompletionDate(date);
    }

    public void starred(boolean isStarred) {
        task.setStarred(isStarred);
    }

    public void user(User user) {
        task.setUser(user);
    }

}

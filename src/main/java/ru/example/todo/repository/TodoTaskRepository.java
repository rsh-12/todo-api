package ru.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todo.entity.TodoTask;

public interface TodoTaskRepository extends JpaRepository<TodoTask, Long> {
}

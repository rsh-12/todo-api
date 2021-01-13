package ru.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todo.entity.TodoSection;

public interface TodoSectionRepository extends JpaRepository<TodoSection, Long> {
}

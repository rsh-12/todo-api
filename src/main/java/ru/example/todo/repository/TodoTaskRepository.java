package ru.example.todo.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todo.entity.TodoTask;

import java.time.LocalDate;
import java.util.List;

public interface TodoTaskRepository extends JpaRepository<TodoTask, Long> {

    List<TodoTask> findAllByCompletionDateEquals(LocalDate date, Pageable page);

    List<TodoTask> findAllByCompletionDateBefore(LocalDate date, Pageable page);
}

package ru.example.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todo.entity.TodoTask;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TodoTaskRepository extends JpaRepository<TodoTask, Long> {

    List<TodoTask> findAllByCompletionDateEqualsAndUserId(LocalDate date, Pageable page, Long userId);

    List<TodoTask> findAllByCompletionDateBeforeAndUserId(LocalDate date, Pageable page, Long userId);

    List<TodoTask> findAllByIdInAndUserId(Set<Long> taskIds, Long userId);

    Page<TodoTask> findAllByUserId(Long userId, Pageable pageable);

    Optional<TodoTask> findByIdAndUserId(Long taskId, Long userId);

}

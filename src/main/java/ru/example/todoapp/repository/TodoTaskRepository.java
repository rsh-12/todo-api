package ru.example.todoapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todoapp.entity.TodoTask;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TodoTaskRepository extends JpaRepository<TodoTask, Long> {

    Page<TodoTask> findAllByCompletionDateEqualsAndUserId(LocalDate date, Long userId, Pageable pageable);

    Page<TodoTask> findAllByCompletionDateBeforeAndUserId(LocalDate date, Long userId, Pageable pageable);

    List<TodoTask> findAllByIdInAndUserId(Set<Long> taskIds, Long userId);

    Page<TodoTask> findAllByUserId(Long userId, Pageable pageable);

    Optional<TodoTask> findByIdAndUserId(Long taskId, Long userId);

    long countByCompleted(boolean isCompleted);

}

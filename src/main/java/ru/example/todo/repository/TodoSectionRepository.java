package ru.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.User;

import java.util.List;
import java.util.Optional;

public interface TodoSectionRepository extends JpaRepository<TodoSection, Long> {

    List<TodoSection> findAllByUserId(Long id);

    List<TodoSection> findAllByUser(User user);

    Optional<TodoSection> findByUserIdAndId(Long userId, Long sectionId);

    void deleteByIdAndUserId(Long sectionId, Long userId);
}

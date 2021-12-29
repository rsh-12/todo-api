package ru.example.todoapp.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.example.todoapp.entity.TodoSection;

public interface TodoSectionRepository extends JpaRepository<TodoSection, Long> {

    Page<TodoSection> findAllByUserId(@Param("userId") Long id, Pageable pageable);

    Optional<TodoSection> findByUserIdAndId(Long userId, Long sectionId);

}

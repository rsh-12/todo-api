package ru.example.todoapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.example.todoapp.dto.TodoSectionDto;
import ru.example.todoapp.entity.TodoSection;

import java.util.Optional;

public interface TodoSectionRepository extends JpaRepository<TodoSection, Long> {

    @Query("select new ru.example.todoapp.dto.TodoSectionDto(s.id, s.title, s.updatedAt, s.createdAt) " +
            "from TodoSection s where s.user.id = :userId")
    Page<TodoSectionDto> findAllByUserIdProjection(@Param("userId") Long id, Pageable pageable);

    Optional<TodoSection> findByUserIdAndId(Long userId, Long sectionId);

    void deleteByIdAndUserId(Long sectionId, Long userId);
}

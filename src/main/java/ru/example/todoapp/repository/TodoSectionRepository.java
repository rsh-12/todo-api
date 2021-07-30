package ru.example.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.example.todoapp.repository.projection.TodoSectionProjection;
import ru.example.todoapp.entity.TodoSection;

import java.util.List;
import java.util.Optional;

public interface TodoSectionRepository extends JpaRepository<TodoSection, Long> {

    @Query("select new ru.example.todoapp.domain.TodoSectionProjection(s.id, s.title, s.updatedAt, s.createdAt) " +
            "from TodoSection s where s.user.id = :userId")
    List<TodoSectionProjection> findAllByUserIdProjection(@Param("userId") Long id);

    Optional<TodoSection> findByUserIdAndId(Long userId, Long sectionId);

    void deleteByIdAndUserId(Long sectionId, Long userId);
}

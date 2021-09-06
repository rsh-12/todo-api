package ru.example.todoapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.repository.projection.TodoSectionProjection;

import java.util.Optional;

public interface TodoSectionRepository extends JpaRepository<TodoSection, Long> {

    @Query("select new ru.example.todoapp.repository.projection.TodoSectionProjection" +
            "(s.id, s.title, s.updatedAt, s.createdAt) " +
            "from TodoSection s where s.user.id = :userId")
    Page<TodoSectionProjection> findAllByUserIdProjection(@Param("userId") Long id, Pageable pageable);

    Optional<TodoSection> findByUserIdAndId(Long userId, Long sectionId);

}

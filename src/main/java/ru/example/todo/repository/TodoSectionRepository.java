package ru.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.User;

import java.util.List;
import java.util.Optional;

public interface TodoSectionRepository extends JpaRepository<TodoSection, Long> {

    @Query("select new ru.example.todo.dto.TodoSectionDto(s.id, s.title, s.updatedAt, s.createdAt) " +
            "from TodoSection s where s.user.id = :userId")
    List<TodoSectionDto> findAllByUserId(@Param("userId") Long id);

    List<TodoSection> findAllByUser(User user);

    Optional<TodoSection> findByUserIdAndId(Long userId, Long sectionId);

    void deleteByIdAndUserId(Long sectionId, Long userId);
}

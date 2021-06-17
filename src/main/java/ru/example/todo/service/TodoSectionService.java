package ru.example.todo.service;

import ru.example.todo.domain.CustomPrincipal;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByOperation;

import java.util.List;

public interface TodoSectionService {

    TodoSection findSectionById(CustomPrincipal principal, Long sectionId);

    List<TodoSectionDto> findSectionDtoList(CustomPrincipal principal);

    void deleteSectionById(CustomPrincipal principal, Long sectionId);

    void createSection(User user, TodoSectionDto sectionDto);

    void updateSection(CustomPrincipal principal, Long sectionId, TodoSectionDto sectionDto);

    void addTasksToOrRemoveFromSection(Long userId, Long sectionId, List<TodoTask> tasks, FilterByOperation flag);
}

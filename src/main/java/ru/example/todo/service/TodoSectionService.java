package ru.example.todo.service;

import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByOperation;

import java.util.List;

public interface TodoSectionService {

    TodoSection findSectionById(User user, Long sectionId);

    List<TodoSectionDto> findSectionDtoList(User user);

    void deleteSectionById(User user, Long sectionId);

    void createSection(User user, TodoSectionDto sectionDto);

    void updateSection(User user, Long sectionId, TodoSectionDto sectionDto);

    void addTasksToOrRemoveFromSection(Long userId, Long sectionId, List<TodoTask> tasks, FilterByOperation flag);
}

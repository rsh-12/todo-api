package ru.example.todo.service;

import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.User;
import ru.example.todo.enums.SetTasks;

import java.util.List;
import java.util.Set;

public interface TodoSectionService {

    TodoSection getSectionById(User user, Long sectionId);

    List<TodoSectionDto> getAllSections(User user);

    void deleteSectionById(User user, Long sectionId);

    void createSection(User user, TodoSectionDto sectionDto);

    void updateSection(User user, Long sectionId, TodoSectionDto sectionDto);

    // add to or remove from the task section
    void moveTasks(Long userId, Long sectionId, Set<Long> tasks, SetTasks flag);
}

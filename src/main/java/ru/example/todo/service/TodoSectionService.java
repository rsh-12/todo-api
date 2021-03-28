package ru.example.todo.service;

import ru.example.todo.entity.TodoSection;
import ru.example.todo.enums.SetTasks;

import java.util.List;
import java.util.Set;

public interface TodoSectionService {

    TodoSection getSectionById(Long userId, Long sectionId);

    List<TodoSection> getAllSections(Long userId);

    void deleteSectionById(Long userId, Long sectionId);

    void createSection(TodoSection section);

    void updateSection(Long userId, Long sectionId, TodoSection section);

    void addTasksToList(Long userId, Long sectionId, Set<Long> tasks, SetTasks flag);
}

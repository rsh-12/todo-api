package ru.example.todo.service;

import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.enums.SetTasks;
import ru.example.todo.security.UserDetailsImpl;

import java.util.List;
import java.util.Set;

public interface TodoSectionService {

    TodoSection getSectionById(UserDetailsImpl uds, Long sectionId);

    List<TodoSection> getAllSections(UserDetailsImpl uds);

    void deleteSectionById(UserDetailsImpl uds, Long sectionId);

    void createSection(TodoSectionDto sectionDto, UserDetailsImpl uds);

    void updateSection(Long userId, Long sectionId, TodoSectionDto sectionDto);

    void addTasksToList(Long userId, Long sectionId, Set<Long> tasks, SetTasks flag);
}

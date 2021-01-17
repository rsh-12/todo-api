package ru.example.todo.service;

import ru.example.todo.entity.TodoSection;
import ru.example.todo.enums.SetTasks;

import java.util.List;
import java.util.Set;

public interface TodoSectionService {

    TodoSection getSectionById(Long sectionId);

    List<TodoSection> getAllSections();

    void deleteSectionById(long sectionId);

    void createSection(TodoSection section);

    void updateSection(Long id, TodoSection putSection);

    void addTasksToList(Long sectionId, Set<Long> tasks, SetTasks flag);
}

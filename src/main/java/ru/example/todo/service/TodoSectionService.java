package ru.example.todo.service;

import ru.example.todo.entity.TodoSection;

import java.util.List;

public interface TodoSectionService {

    TodoSection getSectionById(Long sectionId);

    List<TodoSection> getAllSections();

    void deleteSectionById(long sectionId);

    void createSection(TodoSection section);
}

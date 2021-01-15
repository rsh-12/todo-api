package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.exception.TodoObjectException;
import ru.example.todo.repository.TodoSectionRepository;
import ru.example.todo.service.TodoSectionService;

import java.util.List;

@Service
public class TodoSectionImpl implements TodoSectionService {

    private static final Logger log = LoggerFactory.getLogger(TodoSectionImpl.class.getName());

    private final TodoSectionRepository todoSectionRepository;

    public TodoSectionImpl(TodoSectionRepository todoSectionRepository) {
        this.todoSectionRepository = todoSectionRepository;
    }

    @Override
    public TodoSection getSectionById(Long sectionId) {
        return todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> new TodoObjectException("Section not found: {}" + sectionId));
    }

    @Override
    public List<TodoSection> getAllSections() {
        return todoSectionRepository.findAll();
    }
}

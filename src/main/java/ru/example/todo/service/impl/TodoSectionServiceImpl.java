package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.example.todo.domain.TodoSectionProjection;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByOperation;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoSectionRepository;
import ru.example.todo.service.TodoSectionService;

import java.util.List;

@Service
public class TodoSectionServiceImpl implements TodoSectionService {

    private static final Logger log = LoggerFactory.getLogger(TodoSectionServiceImpl.class.getName());

    private final TodoSectionRepository todoSectionRepository;

    public TodoSectionServiceImpl(TodoSectionRepository todoSectionRepository) {
        this.todoSectionRepository = todoSectionRepository;
    }

    // get section by id
    @Override
    public TodoSection findSectionById(Long userId, Long sectionId) {
        log.info("Get the section by id: {}", sectionId);
        return todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .orElseThrow(() -> new CustomException("Section not found: " + sectionId, HttpStatus.NOT_FOUND));
    }

    // get all sections
    @Override
    public List<TodoSectionProjection> findSections(Long userId) {
        List<TodoSectionProjection> sections = todoSectionRepository.findAllByUserIdProjection(userId);
        log.info("Get all sections: {}", sections.size());
        return sections;
    }

    // delete section by id
    @Override
    public void deleteSectionById(User principal, Long sectionId) {
        todoSectionRepository.deleteById(sectionId);
    }


    // create new section
    @Override
    public TodoSection createSection(User user, TodoSection section) {
        section.setUser(user);
        log.info("Create a new section");
        return todoSectionRepository.save(section);
    }

    // update section title
    @Override
    public TodoSection updateSection(User principal, Long sectionId, TodoSection section) {
        section.setId(sectionId);
        section.setUser(principal);
        return todoSectionRepository.save(section);
    }

    // add to or remove from the task section
    @Override
    public void addTasksToOrRemoveFromSection(Long userId, Long sectionId, List<TodoTask> tasks, FilterByOperation flag) {
        log.info("Get the section by id: {}", sectionId);
        TodoSection section = todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .orElseThrow(() -> new CustomException("Section not found: " + sectionId, HttpStatus.NOT_FOUND));

        // add or remove
        if (flag.equals(FilterByOperation.MOVE)) {
            log.info("Add tasks to the section");
            section.setTodoTasks(tasks);
        } else if (flag.equals(FilterByOperation.REMOVE)) {
            log.info("Remove tasks from the section");
            section.removeTodoTasks(tasks);
        }

        todoSectionRepository.save(section);
    }

}

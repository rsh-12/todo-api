package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.SetTasks;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoSectionRepository;
import ru.example.todo.service.TodoSectionService;
import ru.example.todo.service.TodoTaskService;

import java.util.List;
import java.util.Set;

@Service
public class TodoSectionServiceImpl extends AbstractServiceClass implements TodoSectionService {

    private static final Logger log = LoggerFactory.getLogger(TodoSectionServiceImpl.class.getName());

    private final TodoSectionRepository todoSectionRepository;
    private final TodoTaskService todoTaskService;

    public TodoSectionServiceImpl(TodoSectionRepository todoSectionRepository,
                                  TodoTaskService todoTaskService) {
        this.todoSectionRepository = todoSectionRepository;
        this.todoTaskService = todoTaskService;
    }

    // get section by id
    @Override
    public TodoSection getSectionById(User user, Long sectionId) {
        log.info("Get the section by id: {}", sectionId);
        return todoSectionRepository.findByUserIdAndId(user.getId(), sectionId)
                .orElseThrow(() -> new CustomException("Not Found", "Section not found: " + sectionId, HttpStatus.NOT_FOUND));
    }

    // get all sections
    @Override
    public List<TodoSectionDto> getAllSections(User user) {

        List<TodoSectionDto> sections = todoSectionRepository.findAllByUserId(user.getId());
        log.info("Get all sections: {}", sections.size());
        return sections;
    }

    // todo uds
    // delete section by id
    @Override
    public void deleteSectionById(User user, Long sectionId) {

        TodoSection section = todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException("Not Found", "Section not found", HttpStatus.NOT_FOUND));

        if (isValidOrAdmin(user, section)) {
            todoSectionRepository.delete(section);
        } else {
            throw new CustomException("Forbidden", "Not enough permissions", HttpStatus.FORBIDDEN);
        }
    }


    // create new section
    @Override
    public void createSection(User user, TodoSectionDto sectionDto) {

        TodoSection section = new TodoSection();
        section.setUser(user);
        section.setTitle(sectionDto.getTitle());

        log.info("Create a new section");
        todoSectionRepository.save(section);
    }

    // update section title
    @Override
    public void updateSection(User user, Long sectionId, TodoSectionDto sectionDto) {

        // get a section by id
        TodoSection section = todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException("Not Found", "Section not found: " + sectionId, HttpStatus.NOT_FOUND));

        if (isValidOrAdmin(user, section.getUser())) {
            section.setTitle(sectionDto.getTitle());
        } else {
            throw new CustomException("Forbidden", "Not enough permissions", HttpStatus.FORBIDDEN);
        }

        todoSectionRepository.save(section);
    }


    // add to or remove from the task section
    @Override
    public void moveTasks(Long userId, Long sectionId, Set<Long> tasks, SetTasks flag) {

        if (tasks == null || tasks.isEmpty()) {
            throw new CustomException("Bad Request", "Tasks IDs are required", HttpStatus.BAD_REQUEST);
        }

        log.info("Get the section by id: {}", sectionId);
        TodoSection section = todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .orElseThrow(() -> new CustomException("Not Found", "Section not found: " + sectionId, HttpStatus.NOT_FOUND));

        List<TodoTask> tasksByIds = todoTaskService.findAllBySetId(tasks, userId);
        log.info("Get tasks list size: {}", tasksByIds.size());

        // add or remove
        addOrRemoveTasks(flag, section, tasksByIds);
        todoSectionRepository.save(section);
    }

}

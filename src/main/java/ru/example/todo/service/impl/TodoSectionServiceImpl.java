package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.example.todo.domain.CustomPrincipal;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByOperation;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoSectionRepository;
import ru.example.todo.service.TodoSectionService;

import java.util.List;

@Service
public class TodoSectionServiceImpl extends AbstractServiceClass implements TodoSectionService {

    private static final Logger log = LoggerFactory.getLogger(TodoSectionServiceImpl.class.getName());

    private final TodoSectionRepository todoSectionRepository;

    public TodoSectionServiceImpl(TodoSectionRepository todoSectionRepository) {
        this.todoSectionRepository = todoSectionRepository;
    }

    // get section by id
    @Override
    public TodoSection findSectionById(CustomPrincipal principal, Long sectionId) {
        log.info("Get the section by id: {}", sectionId);
        return todoSectionRepository.findByUserIdAndId(principal.getId(), sectionId)
                .orElseThrow(() -> new CustomException("Not Found", "Section not found: " + sectionId, HttpStatus.NOT_FOUND));
    }

    // get all sections
    @Override
    public List<TodoSectionDto> findSectionDtoList(CustomPrincipal principal) {
        List<TodoSectionDto> sections = todoSectionRepository.findAllByUserId(principal.getId());
        log.info("Get all sections: {}", sections.size());
        return sections;
    }

    // todo uds
    // delete section by id
    @Override
    public void deleteSectionById(CustomPrincipal principal, Long sectionId) {
        TodoSection section = todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException("Not Found", "Section not found", HttpStatus.NOT_FOUND));

        User sectionUser = section.getUser();
        if (sectionUser != null && isUserValidOrHasRoleAdmin(principal, sectionUser.getUsername())) {
            todoSectionRepository.delete(section);
        } else {
            throw new CustomException("Forbidden", "Not enough permissions", HttpStatus.FORBIDDEN);
        }
    }


    // create new section
    @Override
    public void createSection(final User user, final TodoSectionDto sectionDto) {
        TodoSection section = new TodoSection();
        section.setUser(user);
        section.setTitle(sectionDto.getTitle());

        log.info("Create a new section");
        todoSectionRepository.save(section);
    }

    // update section title
    @Override
    public void updateSection(CustomPrincipal principal, Long sectionId, TodoSectionDto sectionDto) {
        // get a section by id
        TodoSection section = todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException("Not Found", "Section not found: " + sectionId, HttpStatus.NOT_FOUND));

        final User sectionUser = section.getUser();
        if (sectionUser != null && isUserValidOrHasRoleAdmin(principal, sectionUser.getUsername())) {
            section.setTitle(sectionDto.getTitle());
        } else {
            throw new CustomException("Forbidden", "Not enough permissions", HttpStatus.FORBIDDEN);
        }

        todoSectionRepository.save(section);
    }


    // add to or remove from the task section
    @Override
    public void addTasksToOrRemoveFromSection(Long userId, Long sectionId, List<TodoTask> tasks, FilterByOperation flag) {
        log.info("Get the section by id: {}", sectionId);
        TodoSection section = todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .orElseThrow(() -> new CustomException("Not Found", "Section not found: " + sectionId, HttpStatus.NOT_FOUND));

        // add or remove
        addOrRemoveTasks(flag, section, tasks);
        todoSectionRepository.save(section);
    }

}

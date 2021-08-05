package ru.example.todoapp.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.example.todoapp.controller.request.TodoSectionRequest;
import ru.example.todoapp.dto.TodoSectionDto;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.enums.filters.FilterByOperation;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoSectionRepository;
import ru.example.todoapp.repository.projection.TodoSectionProjection;
import ru.example.todoapp.service.TodoSectionService;

import java.util.List;

import static ru.example.todoapp.enums.filters.FilterByOperation.MOVE;
import static ru.example.todoapp.enums.filters.FilterByOperation.REMOVE;
import static ru.example.todoapp.service.impl.util.ServiceUtil.validateUser;

@Service
public class TodoSectionServiceImpl implements TodoSectionService {

    private static final Logger log = LoggerFactory.getLogger(TodoSectionServiceImpl.class.getName());

    private final TodoSectionRepository todoSectionRepository;
    private final AuthUserFacade authUserFacade;

    public TodoSectionServiceImpl(TodoSectionRepository todoSectionRepository, AuthUserFacade authUserFacade) {
        this.todoSectionRepository = todoSectionRepository;
        this.authUserFacade = authUserFacade;
    }

    // get section by id
    @Override
    public TodoSection findSectionById(Long sectionId) {
        log.info("Get the section by id: {}", sectionId);
        return todoSectionRepository.findByUserIdAndId(authUserFacade.getUserId(), sectionId)
                .orElseThrow(() -> CustomException.notFound("Section not found: " + sectionId));
    }

    // get all sections
    @Override
    public List<TodoSectionProjection> findSections() {
        Long userId = authUserFacade.getUserId();
        List<TodoSectionProjection> sections = todoSectionRepository.findAllByUserIdProjection(userId);
        log.info("Get all sections: {}", sections.size());
        return sections;
    }

    // delete section by id
    @Override
    public void deleteSectionById(Long sectionId) {
        TodoSection section = todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> CustomException.notFound("Section not found"));
        validateUser(authUserFacade.getLoggedUser(), section.getUser());
        todoSectionRepository.deleteById(sectionId);
    }

    // create new section
    @Override
    public TodoSection createSection(TodoSectionRequest sectionRequest) {
        TodoSection section = new TodoSection(sectionRequest.getTitle());
        section.setUser(authUserFacade.getLoggedUser());
        log.info("Create a new section");
        return todoSectionRepository.save(section);
    }

    // update section title
    @Override
    public TodoSection updateSection(Long sectionId, TodoSectionRequest sectionRequest) {
        TodoSection section = todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> CustomException.notFound("Section not found: id=" + sectionId));
        validateUser(authUserFacade.getLoggedUser(), section.getUser());

        section.setTitle(section.getTitle());
        section.setUser(authUserFacade.getLoggedUser());
        return todoSectionRepository.save(section);
    }

    // add to or remove from the task section
    @Override
    public void addTasksToOrRemoveFromSection(Long userId, Long sectionId, List<TodoTask> tasks, FilterByOperation flag) {
        log.info("Get the section by id: {}", sectionId);
        TodoSection section = todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .orElseThrow(() -> CustomException.notFound("Section not found"));

        // add or remove
        if (flag.equals(MOVE)) {
            log.info("Add tasks to the section");
            section.setTodoTasks(tasks);
        } else if (flag.equals(REMOVE)) {
            log.info("Remove tasks from the section");
            section.removeTodoTasks(tasks);
        } else {
            throw CustomException.internalServerError("Flag not found/Bad request");
        }
        todoSectionRepository.save(section);
    }

    @Override
    public TodoSectionDto mapToSectionDto(TodoSectionProjection projection) {
        return new TodoSectionDto(projection.id(),
                projection.title(),
                projection.updatedAt(),
                projection.createdAt());
    }

    @Override
    public TodoSectionDto mapToSectionDto(TodoSection section) {
        return new TodoSectionDto(section.getId(),
                section.getTitle(),
                section.getUpdatedAt(),
                section.getCreatedAt());
    }

}

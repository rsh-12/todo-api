package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.example.todo.domain.TodoSectionProjection;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.filters.FilterByOperation;
import ru.example.todo.exception.CustomException;
import ru.example.todo.facade.AuthUserFacade;
import ru.example.todo.repository.TodoSectionRepository;
import ru.example.todo.service.TodoSectionService;

import java.util.List;

import static ru.example.todo.enums.filters.FilterByOperation.MOVE;
import static ru.example.todo.enums.filters.FilterByOperation.REMOVE;
import static ru.example.todo.service.impl.util.ServiceUtil.validateUser;

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
    // todo: fix section deleting
    @Override
    public void deleteSectionById(Long sectionId) {
        validateUser(authUserFacade.getLoggedUser(), sectionId, todoSectionRepository);
        todoSectionRepository.deleteById(sectionId);
    }


    // create new section
    @Override
    public TodoSection createSection(TodoSection section) {
        section.setUser(authUserFacade.getLoggedUser());
        log.info("Create a new section");
        return todoSectionRepository.save(section);
    }

    // update section title
    @Override
    public TodoSection updateSection(Long sectionId, TodoSection section) {
        section.setId(sectionId);
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

}

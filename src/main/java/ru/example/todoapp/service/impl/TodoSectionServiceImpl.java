package ru.example.todoapp.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.example.todoapp.controller.request.TodoSectionRequest;
import ru.example.todoapp.dto.TodoSectionDto;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.enums.filters.FilterByOperation;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoSectionRepository;
import ru.example.todoapp.repository.projection.TodoSectionProjection;
import ru.example.todoapp.service.TodoSectionService;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static ru.example.todoapp.enums.Role.ADMIN;
import static ru.example.todoapp.enums.filters.FilterByOperation.MOVE;
import static ru.example.todoapp.enums.filters.FilterByOperation.REMOVE;

@Service
public class TodoSectionServiceImpl implements TodoSectionService {

    private static final Logger log = LoggerFactory.getLogger(TodoSectionServiceImpl.class);

    private final TodoSectionRepository todoSectionRepository;
    private final AuthUserFacade authUserFacade;

    public TodoSectionServiceImpl(TodoSectionRepository todoSectionRepository, AuthUserFacade authUserFacade) {
        this.todoSectionRepository = todoSectionRepository;
        this.authUserFacade = authUserFacade;
    }

    // get section by id
    @Override
    public Optional<TodoSection> findSectionById(Long sectionId) {
        log.info("Get the section by id: {}", sectionId);
        return todoSectionRepository.findByUserIdAndId(authUserFacade.getUserId(), sectionId);
    }

    // get all sections
    @Override
    public Page<TodoSectionProjection> findSections(Pageable pageable) {
        Long userId = authUserFacade.getUserId();
        return todoSectionRepository.findAllByUserIdProjection(userId, pageable);
    }

    // delete section by id
    @Override
    public void deleteSectionById(Long sectionId) {
        Predicate<User> predicate = user -> user.equals(authUserFacade.getLoggedUser());
        Predicate<User> combinedPredicates = predicate.or(user -> user.getRoles().contains(ADMIN));

        todoSectionRepository.findById(sectionId).map(TodoSection::getUser)
                .filter(combinedPredicates)
                .ifPresentOrElse(user -> todoSectionRepository.deleteById(sectionId), () -> {
                    throw CustomException.notFound("Section not found");
                });
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
    public Optional<TodoSection> updateSection(Long sectionId, TodoSectionRequest sectionRequest) {
        Predicate<User> predicate = user -> user.equals(authUserFacade.getLoggedUser());
        Predicate<User> combinedPredicates = predicate.or(user -> user.getRoles().contains(ADMIN));

        return todoSectionRepository.findById(sectionId)
                .filter(section -> combinedPredicates.test(section.getUser()))
                .map(section -> updateTitleAndUser(sectionRequest, section));
    }

    private TodoSection updateTitleAndUser(TodoSectionRequest sectionRequest, TodoSection section) {
        section.setTitle(sectionRequest.getTitle());
        section.setUser(authUserFacade.getLoggedUser());
        return todoSectionRepository.save(section);
    }

    // add to or remove from the task section
    @Override
    public void addTasksToOrRemoveFromSection(Long userId, Long sectionId,
                                              List<TodoTask> tasks, FilterByOperation flag) {
        todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .ifPresent(section -> {
                    if (flag == MOVE) section.setTodoTasks(tasks);
                    else if (flag == REMOVE) section.removeTodoTasks(tasks);
                    todoSectionRepository.save(section);
                });
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

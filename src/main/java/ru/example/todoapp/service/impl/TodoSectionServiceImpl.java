package ru.example.todoapp.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.todoapp.domain.request.TodoSectionRequest;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoSectionRepository;
import ru.example.todoapp.service.TodoSectionService;
import ru.example.todoapp.util.Combinators;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
    @Cacheable(cacheNames = "sections", key = "#sectionId")
    @Transactional(readOnly = true)
    public Optional<TodoSection> findOne(Long sectionId) {
        log.info("Get the section by id: {}", sectionId);

        return todoSectionRepository.findByUserIdAndId(authUserFacade.getUserId(), sectionId);
    }

    // get all sections
    @Override
    @Transactional(readOnly = true)
    public Page<TodoSection> findAll(Pageable pageable) {
        Long userId = authUserFacade.getUserId();
        return todoSectionRepository.findAllByUserId(userId, pageable);
    }

    // delete section by id
    @Override
    @CacheEvict(cacheNames = "sections", key = "#id")
    public void delete(Long id) {
        todoSectionRepository.findById(id).map(TodoSection::getUser)
                .filter(Combinators.checkUserAccess(authUserFacade.getLoggedUser()))
                .ifPresentOrElse(user -> todoSectionRepository.deleteById(id), () -> {
                    throw CustomException.createNotFoundExc("Section not found");
                });
    }

    // create new section
    @Override
    public TodoSection create(TodoSectionRequest sectionRequest) {
        TodoSection section = new TodoSection(sectionRequest.getTitle());
        section.setUser(authUserFacade.getLoggedUser());
        log.info("Create a new section");
        return todoSectionRepository.save(section);
    }

    // update section title
    @Override
    @CachePut(cacheNames = "sections", key = "#sectionId")
    public Optional<TodoSection> update(Long sectionId, TodoSectionRequest sectionRequest) {
        Optional<TodoSection> sectionOptional = todoSectionRepository.findById(sectionId);

        return sectionOptional.map(TodoSection::getUser)
                .filter(Combinators.checkUserAccess(authUserFacade.getLoggedUser()))
                .map(user -> {
                    TodoSection section = sectionOptional.get();
                    section.setUser(user);
                    section.setTitle(sectionRequest.getTitle());
                    return todoSectionRepository.save(section);
                });
    }

    // add to or remove from the task section
    @Override
    public void addTasks(Long userId, Long sectionId, List<TodoTask> tasks) {
        todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .ifPresent(section -> {
                    tasks.forEach(section::addTodoTask);
                    todoSectionRepository.save(section);
                });
    }

    @Override
    public void removeTasks(Long userId, Long sectionId, List<TodoTask> tasks) {
        todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .ifPresent(section -> {
                    section.removeTodoTasks(tasks);
                    todoSectionRepository.save(section);
                });
    }

}

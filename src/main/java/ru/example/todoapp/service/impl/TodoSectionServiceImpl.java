package ru.example.todoapp.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.example.todoapp.facade.AuthUserFacade;
import ru.example.todoapp.repository.TodoSectionRepository;
import ru.example.todoapp.service.TodoSectionService;
import ru.example.todoapp.service.UserService;
import ru.example.todoapp.util.ServiceUtil;


@Slf4j
@Service
@Transactional
public class TodoSectionServiceImpl implements TodoSectionService {

    private final TodoSectionRepository sectionRepository;
    private final AuthUserFacade currentUser;
    private final UserService userService;
    private final ServiceUtil serviceUtil;

    @Autowired
    public TodoSectionServiceImpl(
            TodoSectionRepository sectionRepository,
            AuthUserFacade currentUser,
            UserService userService,
            ServiceUtil serviceUtil) {

        this.sectionRepository = sectionRepository;
        this.currentUser = currentUser;
        this.userService = userService;
        this.serviceUtil = serviceUtil;
    }

    // get section by id
    @Override
    @Cacheable(cacheNames = "sections", key = "#sectionId")
    @Transactional(readOnly = true)
    public Optional<TodoSection> findOne(Long sectionId) {
        log.info("findOne: getting a section with ID: {}", sectionId);
        return sectionRepository.findById(sectionId)
                .filter(section -> serviceUtil.ownerOrAdmin(section.getUserId()));
    }

    // get all sections
    @Override
    @Transactional(readOnly = true)
    public Page<TodoSection> findAll(Pageable pageable) {
        if (currentUser.containsRoleAdmin()) {
            log.info("findAll: getting all sections");
            return sectionRepository.findAll(pageable);
        }

        Long userId = currentUser.getId();
        log.info("findAll: getting all sections by userId: {}", userId);

        return sectionRepository.findAllByUserId(userId, pageable);
    }

    // delete section by id
    @Override
    @CacheEvict(cacheNames = "sections", key = "#sectionId")
    public void delete(Long sectionId) {
        log.info("delete: deleting a section with ID: {}", sectionId);

        sectionRepository.findById(sectionId)
                .map(TodoSection::getUserId)
                .filter(serviceUtil::ownerOrAdmin)
                .ifPresent(sectionRepository::deleteById);
    }

    // create new section
    @Override
    public TodoSection create(TodoSectionRequest sectionRequest) {
        log.info("create: creating a new section");
        throw new AssertionError("Not implemented");
    }

    // update section title
    @Override
    @CachePut(cacheNames = "sections", key = "#sectionId")
    public Optional<TodoSection> update(Long sectionId, TodoSectionRequest sectionRequest) {
        log.info("update: updating the section with ID: {}", sectionId);

        return sectionRepository.findById(sectionId)
                .filter(section -> serviceUtil.ownerOrAdmin(section.getUserId()))
                .map(section -> {
                    section.setTitle(sectionRequest.getTitle());
                    return section;
                });
    }

    // add to or remove from the task section
    @Override
    public void addTasks(Long userId, Long sectionId, List<TodoTask> tasks) {
        sectionRepository.findByUserIdAndId(userId, sectionId)
                .ifPresent(section -> {
                    tasks.forEach(section::addTodoTask);
                    sectionRepository.save(section);
                });
    }

    @Override
    public void removeTasks(Long userId, Long sectionId, List<TodoTask> tasks) {
        sectionRepository.findByUserIdAndId(userId, sectionId)
                .ifPresent(section -> {
                    section.removeTodoTasks(tasks);
                    sectionRepository.save(section);
                });
    }

}

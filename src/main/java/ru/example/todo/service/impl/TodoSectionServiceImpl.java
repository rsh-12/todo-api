package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.Role;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.SetTasks;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.TodoSectionRepository;
import ru.example.todo.repository.UserRepository;
import ru.example.todo.service.TodoSectionService;
import ru.example.todo.service.TodoTaskService;

import java.util.List;
import java.util.Set;

@Service
public class TodoSectionServiceImpl implements TodoSectionService {

    private static final Logger log = LoggerFactory.getLogger(TodoSectionServiceImpl.class.getName());

    private final TodoSectionRepository todoSectionRepository;
    private final TodoTaskService todoTaskService;
    private final UserRepository userRepository;

    public TodoSectionServiceImpl(TodoSectionRepository todoSectionRepository,
                                  TodoTaskService todoTaskService, UserRepository userRepository) {
        this.todoSectionRepository = todoSectionRepository;
        this.todoTaskService = todoTaskService;
        this.userRepository = userRepository;
    }

    // get section by id
    @Override
    public TodoSection getSectionById(Long userId, Long sectionId) {
        log.info("Get the section by id: {}", sectionId);
        TodoSection section = todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .orElseThrow(() -> new CustomException("Section not found: " + sectionId, HttpStatus.NOT_FOUND));
//        return modelMapper.map(section, TodoSectionDto.class);
        return section;
    }

    // get all sections
    @Override
    public List<TodoSection> getAllSections(Long userId) {
        List<TodoSection> sections = todoSectionRepository.findAllByUserId(userId);
        log.info("Get all sections: {}", sections.size());
        return sections;
    }

    // delete section by id
    @Override
    public void deleteSectionById(Long userId, Long sectionId) {

        TodoSection section = todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException("Section not found", HttpStatus.NOT_FOUND));

        if (section.getUser() != null && section.getUser().getId().equals(userId)) {
            todoSectionRepository.delete(section);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException("User not found", HttpStatus.INTERNAL_SERVER_ERROR));

            if (user.getRoles().contains(Role.ROLE_ADMIN)) {
                todoSectionRepository.delete(section);
            } else {
                throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
            }
        }

    }

    // create new section
    @Override
    public void createSection(TodoSectionDto sectionDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        TodoSection section = new TodoSection();
        if (sectionDto.getTitle() == null) {
            throw new CustomException("Title cannot be empty", HttpStatus.BAD_REQUEST);
        }

        section.setUser(user);
        section.setTitle(sectionDto.getTitle());
        log.info("Create a new section");
        todoSectionRepository.save(section);
    }

    // update section title
    @Override
    public void updateSection(Long userId, Long sectionId, TodoSection section) {

        log.info("Get the section by id: {}", sectionId);
        TodoSection sectionFromBd = todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .orElseThrow(() -> new CustomException("Section not found: " + sectionId, HttpStatus.NOT_FOUND));

        sectionFromBd.setTitle(section.getTitle());

        log.info("Save an updated section");
        todoSectionRepository.save(section);
    }

    // add to or remove from the task section
    @Override
    public void addTasksToList(Long userId, Long sectionId, Set<Long> tasks, SetTasks flag) {

        if (tasks == null || tasks.isEmpty()) {
            throw new CustomException("Tasks IDs are required!", HttpStatus.BAD_REQUEST);
        }

        log.info("Get the section by id: {}", sectionId);
        TodoSection section = todoSectionRepository.findByUserIdAndId(userId, sectionId)
                .orElseThrow(() -> new CustomException("Section not found: " + sectionId, HttpStatus.NOT_FOUND));

        List<TodoTask> tasksByIds = todoTaskService.findAllBySetId(tasks, userId);
        log.info("Get tasks list size: {}", tasksByIds.size());

        // add or remove
        addOrRemoveTasks(flag, section, tasksByIds);
    }

    // --------------------------------------------- helper methods
    private void addOrRemoveTasks(SetTasks flag, TodoSection section, List<TodoTask> tasksByIds) {

        if (flag.equals(SetTasks.MOVE)) {
            log.info("Add tasks to the section");
            section.setTodoTasks(tasksByIds);
        } else if (flag.equals(SetTasks.REMOVE)) {
            log.info("Remove tasks from the section");
            section.removeTodoTasks(tasksByIds);
        }

        todoSectionRepository.save(section);
    }
}

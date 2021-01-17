package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.SetTasks;
import ru.example.todo.exception.TodoObjectException;
import ru.example.todo.repository.TodoSectionRepository;
import ru.example.todo.service.TodoSectionService;
import ru.example.todo.service.TodoTaskService;

import java.util.List;
import java.util.Set;

@Service
public class TodoSectionServiceImpl implements TodoSectionService {

    private static final Logger log = LoggerFactory.getLogger(TodoSectionServiceImpl.class.getName());

    private final TodoSectionRepository todoSectionRepository;
    private final TodoTaskService todoTaskService;

    public TodoSectionServiceImpl(TodoSectionRepository todoSectionRepository, TodoTaskService todoTaskService) {
        this.todoSectionRepository = todoSectionRepository;
        this.todoTaskService = todoTaskService;
    }

    // get section by id
    @Override
    public TodoSection getSectionById(Long sectionId) {
        log.info(">>> Get section by id: {}", sectionId);
        return todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> new TodoObjectException("Section not found: " + sectionId));
    }

    // get all sections
    @Override
    public List<TodoSection> getAllSections() {
        List<TodoSection> sections = todoSectionRepository.findAll();
        log.info(">>> Get all sections: {}", sections.size());
        return sections;
    }

    // delete section by id
    @Override
    public void deleteSectionById(long sectionId) {
        if (todoSectionRepository.existsById(sectionId)) {
            log.info(">>> Delete section by id: {}", sectionId);
            todoSectionRepository.deleteById(sectionId);
        }
    }

    // create new section
    @Override
    public void createSection(TodoSection section) {
        log.info(">>> Create new section");
        todoSectionRepository.save(section);
    }


    // update section title
    @Override
    public void updateSection(Long id, TodoSection putSection) {

        log.info(">>> Get section by id: {}", id);
        TodoSection section = todoSectionRepository.findById(id)
                .orElseThrow(() -> new TodoObjectException(("Section not found: " + id)));

        section.setTitle(putSection.getTitle());

        log.info(">>> Save updated section");
        todoSectionRepository.save(section);
    }

    // add to or remove from the task section
    @Override
    public void addTasksToList(Long sectionId, Set<Long> tasks, SetTasks flag) {

        if (tasks == null) {
            log.error(">>> Tasks IDs are emtpy");
            throw new TodoObjectException("Tasks IDs are required!");
        }

        log.info(">>> Get section by id: {}", sectionId);
        TodoSection section = todoSectionRepository.findById(sectionId)
                .orElseThrow(() -> new TodoObjectException("Section not found: " + sectionId));

        List<TodoTask> tasksByIds = todoTaskService.findAllBySetId(tasks);
        log.info(">>> Get tasks list: {}", tasksByIds.size());

        // add or remove
        defineOperation(flag, section, tasksByIds);

        log.info(">>> Set tasks");
        todoSectionRepository.save(section);
    }

    // --------------------------------------------- helper methods
    private void defineOperation(SetTasks flag, TodoSection section, List<TodoTask> tasksByIds) {
        if (flag.equals(SetTasks.MOVE)) {
            log.info(">>> Add tasks to the section");
            section.setTodoTasks(tasksByIds);
        } else if (flag.equals(SetTasks.REMOVE)) {
            log.info(">>> Remove tasks from the section");
            section.removeTodoTasks(tasksByIds);
        }
    }
}

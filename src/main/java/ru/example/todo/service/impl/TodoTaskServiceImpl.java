package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.exception.TodoObjectException;
import ru.example.todo.repository.TodoTaskRepository;
import ru.example.todo.service.TodoTaskService;

import java.time.LocalDate;
import java.util.List;

@Service
public class TodoTaskServiceImpl implements TodoTaskService {

    private static final Logger log = LoggerFactory.getLogger(TodoTaskServiceImpl.class.getName());

    private final TodoTaskRepository todoTaskRepository;

    @Autowired
    public TodoTaskServiceImpl(TodoTaskRepository todoTaskRepository) {
        this.todoTaskRepository = todoTaskRepository;
    }

    @Override
    public List<TodoTask> getAllTasks() {
        List<TodoTask> tasks = todoTaskRepository.findAll(Sort.by("createdAt").descending());
        log.info(">>> Get all tasks: {}", tasks.size());
        return tasks;
    }

    @Override
    public TodoTask getTaskById(Long id) {
        return todoTaskRepository.findById(id)
                .orElseThrow(() -> new TodoObjectException("Task not found: " + id));
    }

    @Override
    public void deleteTaskById(Long id) {
        if (todoTaskRepository.existsById(id)) {
            todoTaskRepository.deleteById(id);
        }
    }

    @Override
    public void save(TodoTask task) {
        log.info(">>> Create new task");
        todoTaskRepository.save(task);
    }

    @Override
    public void createTask(TodoTask newTask) {
        if (newTask.getCompletionDate().isBefore(LocalDate.now())) {
            throw new TodoObjectException("Invalid completion date!");
        }
        todoTaskRepository.save(newTask);
    }

    @Override
    public void updateTask(TodoTask patch, Long id) {
        TodoTask taskFromDB = todoTaskRepository.findById(id)
                .orElseThrow(() -> new TodoObjectException("Task not found: " + id));

        if (patch.getTitle() != null) taskFromDB.setTitle(patch.getTitle());

        if (patch.getCompletionDate() != null) taskFromDB.setCompletionDate(patch.getCompletionDate());

        if (patch.getCompletionDate() != null && patch.getCompletionDate().isBefore(LocalDate.now())) {
            throw new TodoObjectException("Something went wrong!");
        } else {
            todoTaskRepository.save(taskFromDB);
        }
    }

}

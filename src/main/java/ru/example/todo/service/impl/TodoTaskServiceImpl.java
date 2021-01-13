package ru.example.todo.service.impl;
/*
 * Date: 1/13/21
 * Time: 6:16 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.repository.TodoTaskRepository;
import ru.example.todo.service.TodoTaskService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class TodoTaskServiceImpl implements TodoTaskService {

    private static final Logger log = LoggerFactory.getLogger(TodoTaskServiceImpl.class.getName());

    private final TodoTaskRepository todoTaskRepository;

    @Autowired
    public TodoTaskServiceImpl(TodoTaskRepository todoTaskRepository) {
        this.todoTaskRepository = todoTaskRepository;
    }

    @Async
    public CompletableFuture<List<TodoTask>> getAllTodos() {
        List<TodoTask> todos = todoTaskRepository.findAll();
        return CompletableFuture.completedFuture(todos);
    }

    @Override
    public List<TodoTask> getListOfAllTodos() {
        return todoTaskRepository.findAll();
    }

    @Override
    public Optional<TodoTask> getOne(Long id) {
        return todoTaskRepository.findById(id);
    }
}

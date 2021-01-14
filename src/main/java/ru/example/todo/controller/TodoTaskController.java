package ru.example.todo.controller;
/*
 * Date: 1/13/21
 * Time: 6:35 PM
 * */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.todo.controller.assemblers.TodoTaskModelAssembler;
import ru.example.todo.exception.TodoTaskNotFoundException;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.service.TodoTaskService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/todos")
public class TodoTaskController {

    private static final Logger log = LoggerFactory.getLogger(TodoTaskController.class.getName());

    private final TodoTaskService todoTaskService;
    private final TodoTaskModelAssembler assembler;

    @Autowired
    public TodoTaskController(TodoTaskService todoTaskService, TodoTaskModelAssembler assembler) {
        this.todoTaskService = todoTaskService;
        this.assembler = assembler;
    }


    @GetMapping(produces = "application/json")
    public CollectionModel<EntityModel<TodoTask>> all() {

        List<EntityModel<TodoTask>> todos = todoTaskService.getListOfAllTodos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(todos, linkTo(methodOn(TodoTaskController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<TodoTask> one(@PathVariable("id") Long id) {
        TodoTask todo = todoTaskService.getOne(id).orElseThrow(() ->
                new TodoTaskNotFoundException(id));
        return assembler.toModel(todo);
    }
}
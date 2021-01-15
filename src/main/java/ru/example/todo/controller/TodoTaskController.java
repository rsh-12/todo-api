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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.todo.controller.assembler.TodoTaskModelAssembler;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.TaskStatus;
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

    // get all tasks
    @GetMapping(produces = "application/json")
    public CollectionModel<EntityModel<TodoTask>> all() {

        List<EntityModel<TodoTask>> todos = todoTaskService.getAllTasks()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(todos,
                linkTo(methodOn(TodoTaskController.class).all()).withSelfRel());
    }

    // get task by id
    @GetMapping(value = "/{id}", produces = "application/json")
    public EntityModel<TodoTask> one(@PathVariable("id") Long id) {
        return assembler.toModel(todoTaskService.getTaskById(id));
    }

    // delete task by id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id) {
        todoTaskService.deleteTaskById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new task
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createTask(@RequestBody TodoTask newTask) {
        todoTaskService.createTask(newTask);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // update task title or task completion date
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateTask(@PathVariable("id") Long id, @RequestBody TodoTask patch) {
        todoTaskService.updateTask(patch, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // update task status (completed, starred)
    @PostMapping(value = "/task/{taskId}", consumes = "application/json")
    public void setTaskStatus(@PathVariable("taskId") Long taskId,
                              @RequestParam(value = "completed", required = false) TaskStatus completed,
                              @RequestParam(value = "starred", required = false) TaskStatus starred) {

        todoTaskService.setTaskStatus(taskId, completed, starred);
    }
}

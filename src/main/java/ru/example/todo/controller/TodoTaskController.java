package ru.example.todo.controller;
/*
 * Date: 1/13/21
 * Time: 6:35 PM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.todo.controller.assembler.TodoTaskModelAssembler;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.TaskDate;
import ru.example.todo.enums.TaskStatus;
import ru.example.todo.service.TodoTaskService;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/todos")
public class TodoTaskController {

    private final TodoTaskService todoTaskService;
    private final TodoTaskModelAssembler assembler;


    @Autowired
    public TodoTaskController(TodoTaskService todoTaskService, TodoTaskModelAssembler assembler) {
        this.todoTaskService = todoTaskService;
        this.assembler = assembler;
    }

    // get all tasks
    @GetMapping(produces = "application/json")
    public CollectionModel<EntityModel<TodoTask>> all(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNo,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "date", required = false, defaultValue = "ALL") TaskDate date,
            @RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort) {

        List<EntityModel<TodoTask>> todos = todoTaskService
                .getAllTasks(pageNo, pageSize, date, sort)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(todos,
                linkTo(methodOn(TodoTaskController.class).all(pageNo, pageSize, date, sort)).withSelfRel());
    }

    // get task by id
    @GetMapping(value = "/{id}", produces = "application/json")
    public EntityModel<TodoTask> one(@PathVariable("id") @Pattern(regexp = "^\\d+$") Long id) {
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
    public ResponseEntity<?> createTask(@Valid @RequestBody TodoTask newTask) {
        todoTaskService.createTask(newTask);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // update task title or task completion date
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateTask(@PathVariable("id") Long id,
                                        @Valid @RequestBody TodoTask patch) {
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

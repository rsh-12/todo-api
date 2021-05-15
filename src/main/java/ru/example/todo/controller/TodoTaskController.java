package ru.example.todo.controller;
/*
 * Date: 1/13/21
 * Time: 6:35 PM
 * */

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.example.todo.controller.assembler.TodoTaskModelAssembler;
import ru.example.todo.dto.TodoTaskDto;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.enums.TaskDate;
import ru.example.todo.enums.TaskStatus;
import ru.example.todo.security.UserDetailsImpl;
import ru.example.todo.service.TodoTaskService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(tags = "Tasks")
@RestController
@RequestMapping("/api/tasks")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class TodoTaskController {

    private final TodoTaskService todoTaskService;
    private final TodoTaskModelAssembler assembler;
    private final ModelMapper modelMapper;

    @Autowired
    public TodoTaskController(TodoTaskService todoTaskService, TodoTaskModelAssembler assembler, ModelMapper modelMapper) {
        this.todoTaskService = todoTaskService;
        this.assembler = assembler;
        this.modelMapper = modelMapper;
    }

    // get all tasks
    @ApiOperation(value = "List tasks", notes = "List all tasks")
    @GetMapping(produces = "application/json")
    public CollectionModel<EntityModel<TodoTask>> all(
            @AuthenticationPrincipal UserDetailsImpl uds,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNo,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "date", required = false, defaultValue = "ALL") TaskDate date,
            @RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort) {

        List<EntityModel<TodoTask>> todos = todoTaskService
                .getAllTasks(uds.getUser(), pageNo, pageSize, date, sort)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(todos,
                linkTo(methodOn(TodoTaskController.class).all(uds, pageNo, pageSize, date, sort)).withSelfRel());
    }

    // get task by id
    @ApiOperation(value = "Find task", notes = "Find the task by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    public EntityModel<TodoTask> one(
            @AuthenticationPrincipal UserDetailsImpl uds,
            @PathVariable("id") Long taskId) {
        return assembler.toModel(todoTaskService.getTaskById(uds.getUser(), taskId));
    }

    // delete task by id
    @ApiOperation(value = "Remove task", notes = "It permits to remove a task")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(
            @AuthenticationPrincipal UserDetailsImpl uds,
            @PathVariable("id") Long taskId) {
        todoTaskService.deleteTaskById(uds.getUser(), taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new task
    @ApiOperation(value = "Create task", notes = "It permits to create a new task")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createTask(
            @AuthenticationPrincipal UserDetailsImpl uds,
            @Valid @RequestBody TodoTaskDto taskDto) {
        todoTaskService.createTask(uds.getUser(), modelMapper.map(taskDto, TodoTask.class));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // update task title or task completion date
    // or
    // update task status (completed, starred)
    @ApiOperation(value = "Update task", notes = "It permits to update a task")
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateTask(
            @AuthenticationPrincipal UserDetailsImpl uds,
            @PathVariable("id") Long taskId,
            @Valid @RequestBody(required = false) TodoTaskDto taskDto,
            @RequestParam(value = "completed", required = false) TaskStatus completed,
            @RequestParam(value = "starred", required = false) TaskStatus starred) {

        todoTaskService.updateTask(uds.getUser(), taskId, taskDto, completed, starred);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

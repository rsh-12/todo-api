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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.example.todo.controller.assembler.TodoTaskModelAssembler;
import ru.example.todo.dto.TodoTaskDto;
import ru.example.todo.entity.TodoTask;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByDate;
import ru.example.todo.security.UserDetailsImpl;
import ru.example.todo.service.TodoTaskService;
import ru.example.todo.service.UserService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(tags = "Tasks")
@RestController
@RequestMapping("/api/tasks")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class TodoTaskController {

    private final UserService userService;
    private final TodoTaskService todoTaskService;
    private final TodoTaskModelAssembler assembler;
    private final ModelMapper modelMapper;

    @Autowired
    public TodoTaskController(UserService userService, TodoTaskService todoTaskService,
                              TodoTaskModelAssembler assembler, ModelMapper modelMapper) {
        this.userService = userService;
        this.todoTaskService = todoTaskService;
        this.assembler = assembler;
        this.modelMapper = modelMapper;
    }

    // todo: filter by completed or starred params
    // get all tasks
    @ApiOperation(value = "List tasks", notes = "List all tasks")
    @GetMapping(produces = "application/json")
    public CollectionModel<EntityModel<TodoTask>> getTasks(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNo,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "date", required = false, defaultValue = "ALL") FilterByDate date,
            @RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort) {

        List<EntityModel<TodoTask>> todos = todoTaskService
                .findTasks(userDetails.getId(), pageNo, pageSize, date, sort)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(todos, linkTo(methodOn(TodoTaskController.class)
                .getTasks(userDetails, pageNo, pageSize, date, sort)).withSelfRel());
    }

    // get task by id
    @ApiOperation(value = "Find task", notes = "Find the task by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    public EntityModel<TodoTask> getTask(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long taskId) {
        return assembler.toModel(todoTaskService.findTaskById(userDetails.getId(), taskId));
    }

    // delete task by id
    @ApiOperation(value = "Remove task", notes = "It permits to remove a task")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable("id") Long taskId) {
        todoTaskService.deleteTaskById(userDetails.getUser(), taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new task
    @ApiOperation(value = "Create task", notes = "It permits to create a new task")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> createTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @Valid @RequestBody TodoTaskDto taskDto) {
        User user = userService.findUserById(userDetails.getId());
        TodoTask task = todoTaskService.createTask(user, modelMapper.map(taskDto, TodoTask.class));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(task.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @ApiOperation(value = "Update task", notes = "It permits to update a task")
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<String> updateTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable("id") Long taskId,
                                             @Valid @RequestBody TodoTaskDto taskDto) {

        TodoTask task = todoTaskService.findTaskById(userDetails.getId(), taskId);
        modelMapper.map(taskDto, task);
        todoTaskService.save(task);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(task.getId()).toUri();

        return ResponseEntity.ok().header("Location", location.toString()).build();
    }

}

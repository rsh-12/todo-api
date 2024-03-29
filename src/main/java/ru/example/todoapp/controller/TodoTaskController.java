package ru.example.todoapp.controller;
/*
 * Date: 1/13/21
 * Time: 6:35 PM
 * */

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.example.todoapp.controller.assembler.TodoTaskModelAssembler;
import ru.example.todoapp.domain.request.TodoTaskRequest;
import ru.example.todoapp.entity.TodoTask;
import ru.example.todoapp.service.TodoTaskService;
import ru.example.todoapp.service.dto.TodoTaskDto;
import ru.example.todoapp.service.mapper.TaskMapper;
import ru.example.todoapp.util.filters.FilterByDate;


@Api(tags = "Tasks")
@RestController
@RequestMapping("/api/tasks")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class TodoTaskController {

    private final TodoTaskService todoTaskService;
    private final TodoTaskModelAssembler assembler;
    private final TaskMapper taskMapper;

    @Autowired
    public TodoTaskController(TodoTaskService todoTaskService, TodoTaskModelAssembler assembler,
            TaskMapper taskMapper) {
        this.todoTaskService = todoTaskService;
        this.assembler = assembler;
        this.taskMapper = taskMapper;
    }

    // get all tasks
    @ApiOperation(value = "List tasks", notes = "List all tasks")
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getTasks(
            @RequestParam(value = "date", required = false, defaultValue = "ALL") FilterByDate date,
            @PageableDefault(sort = {"createdAt"}) Pageable pageable,
            PagedResourcesAssembler<TodoTaskDto> pra) {

        Page<TodoTaskDto> tasks = todoTaskService
                .findAll(date, pageable)
                .map(taskMapper::mapToTaskDto);

        return ResponseEntity.ok()
                .body(pra.toModel(tasks, assembler));
    }

    // get task by id
    @ApiOperation(value = "Find task", notes = "Find the task by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<EntityModel<TodoTaskDto>> getTask(@PathVariable("id") Long taskId) {
        var model = todoTaskService.findOne(taskId)
                .map(task -> assembler.toModel(taskMapper.mapToTaskDto(task)));

        return ResponseEntity.of(model);
    }

    // delete task by id
    @ApiOperation(value = "Remove task", notes = "It permits to remove a task")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long taskId) {
        todoTaskService.delete(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new task
    @ApiOperation(value = "Create task", notes = "It permits to create a new task")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> createTask(@Valid @RequestBody TodoTaskRequest taskRequest) {
        TodoTask task = todoTaskService.create(taskRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(task.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @ApiOperation(value = "Update task", notes = "It permits to update a task")
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<String> updateTask(
            @PathVariable("id") Long taskId,
            @Valid @RequestBody TodoTaskRequest taskRequest) {

        URI location = todoTaskService.update(taskId, taskRequest)
                .map(task -> ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .buildAndExpand(task.getId())
                        .toUri())
                .orElse(URI.create(""));

        return ResponseEntity.ok().header(HttpHeaders.LOCATION, location.toString()).build();
    }

}

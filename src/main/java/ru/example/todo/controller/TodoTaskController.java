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
import ru.example.todo.exception.TodoObjectException;
import ru.example.todo.service.TodoTaskService;

import java.time.LocalDate;
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

        List<EntityModel<TodoTask>> todos = todoTaskService.getAllTasks().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(todos,
                linkTo(methodOn(TodoTaskController.class).all()).withSelfRel());
    }

    // get task by id
    @GetMapping(value = "/{id}", produces = "application/json")
    public EntityModel<TodoTask> one(@PathVariable("id") Long id) {
        TodoTask todo = todoTaskService.getTaskById(id).orElseThrow(() ->
                new TodoObjectException("Task not found: " + id));
        return assembler.toModel(todo);
    }

    // delete task by id
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable Long id) {
        if (todoTaskService.existsById(id))
            todoTaskService.deleteTaskById(id);
    }

    // create new task
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createTask(@RequestBody TodoTask newTask) {
        if (newTask.getCompletionDate().isBefore(LocalDate.now())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        todoTaskService.save(newTask);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // update task title or task completion date
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public TodoTask updateTask(@PathVariable("id") Long id, @RequestBody TodoTask patch) {
        TodoTask taskFromDB = todoTaskService.getTaskById(id)
                .orElseThrow(() -> new TodoObjectException("Task not found: " + id));

        if (patch.getTitle() != null) taskFromDB.setTitle(patch.getTitle());

        if (patch.getCompletionDate() != null) taskFromDB.setCompletionDate(patch.getCompletionDate());

        if (patch.getCompletionDate() != null && patch.getCompletionDate().isBefore(LocalDate.now())) {
            throw new TodoObjectException("Something went wrong!");
        } else {
            return todoTaskService.save(taskFromDB);
        }
    }

    // update completed field
    @PostMapping("/complete/{id}")
    public ResponseEntity<?> setCompleted(@PathVariable Long id) {
        TodoTask task = todoTaskService.getTaskById(id)
                .orElseThrow(() -> new TodoObjectException("Task not found: " + id));
        task.setCompleted(!task.isCompleted());
        todoTaskService.save(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // update starred field
    @PostMapping("/mark/{id}")
    public ResponseEntity<?> setStarred(@PathVariable Long id) {
        TodoTask task = todoTaskService.getTaskById(id)
                .orElseThrow(() -> new TodoObjectException("Task not found: " + id));
        task.setStarred(!task.isStarred());
        todoTaskService.save(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}

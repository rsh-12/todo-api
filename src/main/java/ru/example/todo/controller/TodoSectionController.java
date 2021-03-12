package ru.example.todo.controller;
/*
 * Date: 1/13/21
 * Time: 7:51 PM
 * */

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.todo.controller.assembler.TodoSectionModelAssembler;
import ru.example.todo.controller.wrapper.TaskIdsWrapper;
import ru.example.todo.domain.TodoSectionRequest;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.enums.SetTasks;
import ru.example.todo.service.TodoSectionService;
import ru.example.todo.util.Views;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(tags = "Task sections")
@RestController
@RequestMapping("/api/sections")
public class TodoSectionController {

    private final TodoSectionService todoSectionService;
    private final TodoSectionModelAssembler assembler;

    @Autowired
    public TodoSectionController(TodoSectionService todoSectionService, TodoSectionModelAssembler assembler) {
        this.todoSectionService = todoSectionService;
        this.assembler = assembler;
    }

    // get all sections
    @ApiOperation(value = "List todo sections", notes = "List all todo sections")
    @GetMapping(produces = "application/json")
    @JsonView(Views.Public.class)
    public CollectionModel<EntityModel<TodoSection>> all() {
        List<EntityModel<TodoSection>> sections = todoSectionService.getAllSections()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(sections,
                linkTo(methodOn(TodoSectionController.class).all()).withSelfRel());
    }

    // get custom section by id
    @ApiOperation(value = "Find section", notes = "Find the Section by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(value = Views.Internal.class)
    public EntityModel<TodoSection> one(@PathVariable("id") Long id) {
        return assembler.toModel(todoSectionService.getSectionById(id));
    }

    // delete section by id
    @ApiOperation(value = "Remove section", notes = "It permits to remove a section")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable long id) {
        todoSectionService.deleteSectionById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new section
    @ApiOperation(value = "Create section", notes = "It permits to create a new section")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createSection(@Valid @RequestBody TodoSectionRequest sectionRequest) {
        todoSectionService.createSection(sectionRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    // update section title by id
    @ApiOperation(value = "Update section", notes = "It permits to update a section")
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateSection(@PathVariable("id") Long sectionId,
                                           @Valid @RequestBody TodoSectionRequest sectionRequest) {
        todoSectionService.updateSection(sectionId, sectionRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: исправить метод
    // add tasks to the list
    @ApiOperation(value = "Add tasks to section", notes = "It permits to add tasks to section")
    @PostMapping(value = "/{id}/tasks", consumes = "application/json")
    public ResponseEntity<?> addTasksToList(@PathVariable("id") Long sectionId,
                                            @RequestBody TaskIdsWrapper wrapper,
                                            @RequestParam(value = "do") SetTasks flag) {

        todoSectionService.addTasksToList(sectionId, wrapper.tasks, flag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

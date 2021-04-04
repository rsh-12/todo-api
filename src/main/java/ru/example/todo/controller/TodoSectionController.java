package ru.example.todo.controller;
/*
 * Date: 1/13/21
 * Time: 7:51 PM
 * */

import com.fasterxml.jackson.annotation.JsonView;
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
import ru.example.todo.controller.assembler.TodoSectionModelAssembler;
import ru.example.todo.controller.wrapper.TaskIdsWrapper;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.enums.SetTasks;
import ru.example.todo.security.UserDetailsImpl;
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
    private final ModelMapper modelMapper;

    @Autowired
    public TodoSectionController(TodoSectionService todoSectionService, TodoSectionModelAssembler assembler, ModelMapper modelMapper) {
        this.todoSectionService = todoSectionService;
        this.assembler = assembler;
        this.modelMapper = modelMapper;
    }

    // get all sections
    @ApiOperation(value = "List todo sections", notes = "List all todo sections")
    @GetMapping(produces = "application/json")
    @JsonView(Views.Public.class)
    public CollectionModel<EntityModel<TodoSection>> all(@AuthenticationPrincipal UserDetailsImpl uds) {

        List<EntityModel<TodoSection>> sections = todoSectionService.getAllSections(uds)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(sections,
                linkTo(methodOn(TodoSectionController.class).all(uds)).withSelfRel());
    }

    // get custom section by id
    @ApiOperation(value = "Find section", notes = "Find the Section by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(value = Views.Internal.class)
    public EntityModel<TodoSection> one(@AuthenticationPrincipal UserDetailsImpl uds,
                                        @PathVariable("id") Long sectonId) {

        return assembler.toModel(todoSectionService.getSectionById(uds, sectonId));
    }

    // delete section by id
    @ApiOperation(value = "Remove section", notes = "It permits to remove a section")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@AuthenticationPrincipal UserDetailsImpl uds,
                                       @PathVariable("id") Long sectionId) {
        todoSectionService.deleteSectionById(uds, sectionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new section
    @ApiOperation(value = "Create section", notes = "It permits to create a new section")
    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> createSection(
            @AuthenticationPrincipal UserDetailsImpl uds,
            @Valid @RequestBody TodoSectionDto sectionDto) {
        todoSectionService.createSection(sectionDto, uds);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    // update section title by id
    @ApiOperation(value = "Update section", notes = "It permits to update a section")
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateSection(
            @AuthenticationPrincipal UserDetailsImpl uds,
            @PathVariable("id") Long sectionId,
            @Valid @RequestBody TodoSectionDto sectionDto) {
        todoSectionService.updateSection(uds.getId(), sectionId, sectionDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // add tasks to the list
    @ApiOperation(value = "Add tasks to section", notes = "It permits to add tasks to section")
    @PostMapping(value = "/{id}/tasks", consumes = "application/json")
    public ResponseEntity<?> addTasksToList(
            @AuthenticationPrincipal UserDetailsImpl uds,
            @PathVariable("id") Long sectionId,
            @RequestBody TaskIdsWrapper wrapper,
            @RequestParam(value = "do") SetTasks flag) {

        todoSectionService.addTasksToList(uds.getId(), sectionId, wrapper.tasks, flag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

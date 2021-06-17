package ru.example.todo.controller;
/*
 * Date: 1/13/21
 * Time: 7:51 PM
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
import ru.example.todo.controller.assembler.TodoSectionModelAssembler;
import ru.example.todo.controller.wrapper.TaskIdsWrapper;
import ru.example.todo.domain.CustomPrincipal;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.entity.User;
import ru.example.todo.enums.filters.FilterByOperation;
import ru.example.todo.facade.TasksFacade;
import ru.example.todo.service.TodoSectionService;
import ru.example.todo.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(tags = "Task sections")
@RestController
@RequestMapping("/api/sections")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class TodoSectionController {

    private final UserService userService;
    private final TodoSectionService todoSectionService;
    private final TasksFacade tasksFacade;
    private final TodoSectionModelAssembler assembler;
    private final ModelMapper mapper;

    @Autowired
    public TodoSectionController(UserService userService, TodoSectionService todoSectionService, TasksFacade tasksFacade,
                                 TodoSectionModelAssembler assembler, ModelMapper mapper) {
        this.userService = userService;
        this.todoSectionService = todoSectionService;
        this.tasksFacade = tasksFacade;
        this.assembler = assembler;
        this.mapper = mapper;
    }

    // get all sections
    @ApiOperation(value = "List todo sections", notes = "List all todo sections")
    @GetMapping(produces = "application/json")
    public CollectionModel<EntityModel<TodoSection>> getSections(@AuthenticationPrincipal CustomPrincipal principal) {

        List<EntityModel<TodoSection>> sections = todoSectionService.findSectionDtoList(principal)
                .stream()
                .map(sectionDto -> mapper.map(sectionDto, TodoSection.class))
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(sections,
                linkTo(methodOn(TodoSectionController.class).getSections(principal)).withSelfRel());
    }

    // get custom section by id
    @ApiOperation(value = "Find section", notes = "Find the Section by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    public EntityModel<TodoSection> getSection(@AuthenticationPrincipal CustomPrincipal principal,
                                               @PathVariable("id") Long sectonId) {

        return assembler.toModel(todoSectionService.findSectionById(principal, sectonId));
    }

    // delete section by id
    @ApiOperation(value = "Remove section", notes = "It permits to remove a section")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSection(@AuthenticationPrincipal CustomPrincipal principal,
                                                @PathVariable("id") Long sectionId) {
        todoSectionService.deleteSectionById(principal, sectionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new section
    @ApiOperation(value = "Create section", notes = "It permits to create a new section")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> createSection(@AuthenticationPrincipal CustomPrincipal principal,
                                                @Valid @RequestBody TodoSectionDto sectionDto) {
        User user = userService.findUserByUsername(principal.getName());
        todoSectionService.createSection(user, sectionDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // update section title by id
    @ApiOperation(value = "Update section", notes = "It permits to update a section")
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<String> updateSection(@AuthenticationPrincipal CustomPrincipal principal,
                                                @PathVariable("id") Long sectionId,
                                                @Valid @RequestBody TodoSectionDto sectionDto) {

        todoSectionService.updateSection(principal, sectionId, sectionDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // add tasks to the list
    @ApiOperation(value = "Add tasks to section", notes = "It permits to add tasks to section")
    @PostMapping(value = "/{id}/tasks", consumes = "application/json")
    public ResponseEntity<String> addOrRemoveTasks(@AuthenticationPrincipal CustomPrincipal principal,
                                                   @PathVariable("id") Long sectionId,
                                                   @RequestBody TaskIdsWrapper wrapper,
                                                   @RequestParam(value = "do") FilterByOperation flag) {

        tasksFacade.addTasksToOrRemoveFromSection(principal.getId(), sectionId, wrapper.tasks, flag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.example.todo.controller.assembler.TodoSectionModelAssembler;
import ru.example.todo.controller.wrapper.TaskIdsWrapper;
import ru.example.todo.dto.TodoSectionDto;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.enums.filters.FilterByOperation;
import ru.example.todo.facade.TasksFacade;
import ru.example.todo.service.TodoSectionService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(tags = "Task sections")
@RestController
@RequestMapping("/api/sections")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class TodoSectionController {

    private final TodoSectionService todoSectionService;
    private final TasksFacade tasksFacade;
    private final TodoSectionModelAssembler assembler;
    private final ModelMapper modelMapper;

    @Autowired
    public TodoSectionController(TodoSectionService todoSectionService, TasksFacade tasksFacade,
                                 TodoSectionModelAssembler assembler, ModelMapper modelMapper) {
        this.todoSectionService = todoSectionService;
        this.tasksFacade = tasksFacade;
        this.assembler = assembler;
        this.modelMapper = modelMapper;
    }

    // get all sections
    @ApiOperation(value = "List todo sections", notes = "List all todo sections")
    @GetMapping(produces = "application/json")
    public CollectionModel<EntityModel<TodoSection>> getSections() {

        List<EntityModel<TodoSection>> sections = todoSectionService.findSections()
                .stream()
                .map(projection -> modelMapper.map(projection, TodoSection.class))
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(sections,
                linkTo(methodOn(TodoSectionController.class).getSections()).withSelfRel());
    }

    // get custom section by id
    @ApiOperation(value = "Find section", notes = "Find the Section by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    public EntityModel<TodoSection> getSection(@PathVariable("id") Long sectionId) {
        return assembler.toModel(todoSectionService.findSectionById(sectionId));
    }

    // delete section by id
    @ApiOperation(value = "Remove section", notes = "It permits to remove a section")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSection(@PathVariable("id") Long sectionId) {
        todoSectionService.deleteSectionById(sectionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new section
    @ApiOperation(value = "Create section", notes = "It permits to create a new section")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> createSection(@Valid @RequestBody TodoSectionDto sectionDto) {
        TodoSection section = todoSectionService.createSection(modelMapper.map(sectionDto, TodoSection.class));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(section.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    // update section title by id
    @ApiOperation(value = "Update section", notes = "It permits to update a section")
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<String> updateSection(@PathVariable("id") Long sectionId,
                                                @Valid @RequestBody TodoSectionDto sectionDto) {

        TodoSection mappedSection = modelMapper.map(sectionDto, TodoSection.class);
        TodoSection section = todoSectionService.updateSection(sectionId, mappedSection);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(section.getId()).toUri();

        return ResponseEntity.ok().header("Location", location.toString()).build();
    }

    // add tasks to the list
    @ApiOperation(value = "Add tasks to section", notes = "It permits to add tasks to section")
    @PostMapping(value = "/{id}/tasks", consumes = "application/json")
    public ResponseEntity<String> addOrRemoveTasks(@PathVariable("id") Long sectionId,
                                                   @RequestBody TaskIdsWrapper wrapper,
                                                   @RequestParam(value = "do") FilterByOperation flag) {

        tasksFacade.addTasksToOrRemoveFromSection(sectionId, wrapper.tasks, flag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

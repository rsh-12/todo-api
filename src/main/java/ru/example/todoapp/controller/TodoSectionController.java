package ru.example.todoapp.controller;
/*
 * Date: 1/13/21
 * Time: 7:51 PM
 * */

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpHeaders;
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
import ru.example.todoapp.controller.assembler.TodoSectionModelAssembler;
import ru.example.todoapp.domain.request.TaskIdsWrapper;
import ru.example.todoapp.domain.request.TodoSectionRequest;
import ru.example.todoapp.service.dto.TodoSectionDto;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.facade.TasksFacade;
import ru.example.todoapp.service.TodoSectionService;
import ru.example.todoapp.util.filters.FilterByOperation;

import javax.validation.Valid;
import java.net.URI;
import java.util.function.Function;

import static ru.example.todoapp.util.filters.FilterByOperation.MOVE;

@Api(tags = "Task sections")
@RestController
@RequestMapping("/api/sections")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class TodoSectionController {

    private final TodoSectionService todoSectionService;
    private final TasksFacade tasksFacade;
    private final TodoSectionModelAssembler assembler;

    @Autowired
    public TodoSectionController(TodoSectionService todoSectionService, TasksFacade tasksFacade,
                                 TodoSectionModelAssembler assembler) {
        this.todoSectionService = todoSectionService;
        this.tasksFacade = tasksFacade;
        this.assembler = assembler;
    }

    // get all sections
    @ApiOperation(value = "List todo sections", notes = "List all todo sections")
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getSections(@PageableDefault Pageable pageable,
                                         PagedResourcesAssembler<TodoSectionDto> pra) {
        Page<TodoSectionDto> sections = todoSectionService.findAll(pageable)
                .map(todoSectionService::mapToSectionDto);

        return ResponseEntity.ok()
                .body(pra.toModel(sections, assembler));
    }

    // get custom section by id
    @ApiOperation(value = "Find section", notes = "Find the Section by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getSection(@PathVariable("id") Long sectionId) {
        return ResponseEntity.of(todoSectionService.findOne(sectionId)
                .map(todoSectionService::mapToSectionDto)
                .map(assembler::toModel));
    }

    // delete section by id
    @ApiOperation(value = "Remove section", notes = "It permits to remove a section")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSection(@PathVariable("id") Long sectionId) {
        todoSectionService.delete(sectionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new section
    @ApiOperation(value = "Create section", notes = "It permits to create a new section")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> createSection(@Valid @RequestBody TodoSectionRequest sectionRequest) {
        TodoSection section = todoSectionService.create(sectionRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(section.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    // update section title by id
    @ApiOperation(value = "Update section", notes = "It permits to update a section")
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateSection(@PathVariable("id") Long sectionId,
                                           @Valid @RequestBody TodoSectionRequest sectionRequest) {
        Function<TodoSection, URI> f = section -> ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(section.getId()).toUri();

        return ResponseEntity.of(todoSectionService.update(sectionId, sectionRequest)
                .map(f)
                .map(uri -> ResponseEntity.ok()
                        .header(HttpHeaders.LOCATION, uri.toString())
                        .build()));
    }

    // todo: rewrite
    // add tasks to the list
    @ApiOperation(value = "Add tasks to section", notes = "It permits to add tasks to section")
    @PostMapping(value = "/{id}/tasks", consumes = "application/json")
    public ResponseEntity<String> addOrRemoveTasks(@PathVariable("id") Long sectionId,
                                                   @RequestBody TaskIdsWrapper wrapper,
                                                   @RequestParam(value = "do") FilterByOperation flag) {
        if (flag == MOVE) {
            tasksFacade.addTasks(sectionId, wrapper.tasks);
        } else {
            tasksFacade.removeTasks(sectionId, wrapper.tasks);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}

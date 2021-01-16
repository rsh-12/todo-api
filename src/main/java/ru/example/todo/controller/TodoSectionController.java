package ru.example.todo.controller;
/*
 * Date: 1/13/21
 * Time: 7:51 PM
 * */

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.example.todo.controller.assembler.TodoSectionModelAssembler;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.service.TodoSectionService;
import ru.example.todo.util.Views;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static ru.example.todo.exception.TodoObjectExceptionHandler.getFieldErrorsHandler;

@RestController
@RequestMapping("api/sections")
public class TodoSectionController {

    private final TodoSectionService todoSectionService;
    private final TodoSectionModelAssembler assembler;

    @Autowired
    public TodoSectionController(TodoSectionService todoSectionService, TodoSectionModelAssembler assembler) {
        this.todoSectionService = todoSectionService;
        this.assembler = assembler;
    }

    // ------------------------------------ handles field errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleException(MethodArgumentNotValidException ex) {
        return getFieldErrorsHandler(ex);
    }


    //     get all custom sections
    @GetMapping(produces = "application/json")
    @JsonView(value = Views.Public.class)
    public CollectionModel<EntityModel<TodoSection>> all() {
        List<EntityModel<TodoSection>> sections = todoSectionService.getAllSections()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(sections,
                linkTo(methodOn(TodoSectionController.class).all()).withSelfRel());
    }

    //     get custom section by id
    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(value = Views.Internal.class)
    public EntityModel<TodoSection> one(@PathVariable("id") Long id) {
        return assembler.toModel(todoSectionService.getSectionById(id));
    }

    // delete section by id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable long id) {
        todoSectionService.deleteSectionById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create new section
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createSection(@Valid @RequestBody TodoSection section) {
        todoSectionService.createSection(section);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    // update section title by id
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateSection(@PathVariable Long id,
                                           @Valid @RequestBody TodoSection putSection) {
        todoSectionService.updateSection(id, putSection);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}

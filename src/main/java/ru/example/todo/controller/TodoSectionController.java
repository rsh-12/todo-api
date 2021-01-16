package ru.example.todo.controller;
/*
 * Date: 1/13/21
 * Time: 7:51 PM
 * */

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.todo.controller.assembler.TodoSectionModelAssembler;
import ru.example.todo.entity.TodoSection;
import ru.example.todo.service.TodoSectionService;
import ru.example.todo.util.Views;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

}

package ru.example.todo.controller.assembler;
/*
 * Date: 1/15/21
 * Time: 11:05 AM
 * */

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.example.todo.controller.TodoSectionController;
import ru.example.todo.entity.TodoSection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TodoSectionModelAssembler implements RepresentationModelAssembler<TodoSection, EntityModel<TodoSection>> {

    @Override
    public EntityModel<TodoSection> toModel(TodoSection section) {
        return EntityModel.of(section,
                linkTo(methodOn(TodoSectionController.class).one(section.getId())).withSelfRel(),
                linkTo(methodOn(TodoSectionController.class).all()).withRel("sections"));
    }
}

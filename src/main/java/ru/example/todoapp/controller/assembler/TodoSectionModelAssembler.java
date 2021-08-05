package ru.example.todoapp.controller.assembler;
/*
 * Date: 1/15/21
 * Time: 11:05 AM
 * */

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.example.todoapp.controller.TodoSectionController;
import ru.example.todoapp.dto.TodoSectionDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TodoSectionModelAssembler implements
        RepresentationModelAssembler<TodoSectionDto, EntityModel<TodoSectionDto>> {

    @Override
    public EntityModel<TodoSectionDto> toModel(TodoSectionDto section) {
        return EntityModel.of(section,
                linkTo(methodOn(TodoSectionController.class).getSection(section.id())).withSelfRel(),
                linkTo(methodOn(TodoSectionController.class).getSections()).withRel("sections"));
    }
}

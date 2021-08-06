package ru.example.todoapp.controller.assembler;
/*
 * Date: 1/13/21
 * Time: 9:31 PM
 * */

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.example.todoapp.controller.TodoTaskController;
import ru.example.todoapp.dto.TodoTaskDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TodoTaskModelAssembler implements RepresentationModelAssembler<TodoTaskDto, EntityModel<TodoTaskDto>> {

    @Override
    public EntityModel<TodoTaskDto> toModel(TodoTaskDto TodoTaskDto) {
        return EntityModel.of(TodoTaskDto,
                linkTo(methodOn(TodoTaskController.class)
                        .getTask(TodoTaskDto.id())).withSelfRel(),
                linkTo(methodOn(TodoTaskController.class)
                        .getTasks(null, null, null, null)).withRel("tasks"));
    }

}

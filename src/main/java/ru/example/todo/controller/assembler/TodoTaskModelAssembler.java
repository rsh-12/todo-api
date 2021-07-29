package ru.example.todo.controller.assembler;
/*
 * Date: 1/13/21
 * Time: 9:31 PM
 * */

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.example.todo.controller.TodoTaskController;
import ru.example.todo.entity.TodoTask;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TodoTaskModelAssembler implements RepresentationModelAssembler<TodoTask, EntityModel<TodoTask>> {

    @Override
    public EntityModel<TodoTask> toModel(TodoTask todoTask) {
        return EntityModel.of(todoTask,
                linkTo(methodOn(TodoTaskController.class)
                        .getTask(todoTask.getId())).withSelfRel(),
                linkTo(methodOn(TodoTaskController.class)
                        .getTasks(null, null, null, null)).withRel("tasks"));
    }

}

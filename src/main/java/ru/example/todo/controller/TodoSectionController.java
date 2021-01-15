package ru.example.todo.controller;
/*
 * Date: 1/13/21
 * Time: 7:51 PM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.todo.service.TodoSectionService;

@RestController
@RequestMapping("api/section")
public class TodoSectionController {

    private final TodoSectionService todoSectionService;

    @Autowired
    public TodoSectionController(TodoSectionService todoSectionService) {
        this.todoSectionService = todoSectionService;
    }

}

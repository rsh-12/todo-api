package ru.example.todo.controller;
/*
 * Date: 3/25/21
 * Time: 4:39 PM
 * */

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.todo.dto.UserDto;
import ru.example.todo.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public String login(@Valid @RequestBody UserDto userDto) {
        return userService.login(userDto);
    }
}

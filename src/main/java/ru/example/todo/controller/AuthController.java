package ru.example.todo.controller;
/*
 * Date: 5/15/21
 * Time: 6:10 PM
 * */

import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.User;
import ru.example.todo.messaging.MessagingService;
import ru.example.todo.messaging.requests.Email;
import ru.example.todo.messaging.requests.Token;
import ru.example.todo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final MessagingService messagingService;
    private final ModelMapper modelMapper;

    public AuthController(UserService userService, MessagingService messagingService, ModelMapper modelMapper) {
        this.userService = userService;
        this.messagingService = messagingService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<String> login(@Valid @RequestBody UserDto userDto) {
        String tokens = userService.login(userDto);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto userDto) {
        String register = userService.register(modelMapper.map(userDto, User.class));
        return ResponseEntity.ok(register);
    }

    @PostMapping(value = "/token", produces = "application/json")
    public ResponseEntity<String> refreshToken(HttpServletRequest request) {
        String tokens = userService.refreshToken(request.getHeader("token"));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/password/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Email email) {
        messagingService.send(email);
        return ResponseEntity.ok("OK");
    }

    @PostMapping(value = "/password/reset")
    public ResponseEntity<?> resetPassword(@RequestParam(value = "token") Token token) {
        messagingService.send(token);
        return ResponseEntity.ok("OK");
    }
}

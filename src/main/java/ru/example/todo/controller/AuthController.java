package ru.example.todo.controller;
/*
 * Date: 5/15/21
 * Time: 6:10 PM
 * */

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.User;
import ru.example.todo.facade.PasswordFacade;
import ru.example.todo.messaging.MessagingService;
import ru.example.todo.messaging.requests.EmailRequest;
import ru.example.todo.messaging.requests.TokenRequest;
import ru.example.todo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Api(tags = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordFacade passwordFacade;
    private final MessagingService messagingService;
    private final ModelMapper modelMapper;

    public AuthController(UserService userService, PasswordFacade passwordFacade,
                          MessagingService messagingService, ModelMapper modelMapper) {
        this.userService = userService;
        this.passwordFacade = passwordFacade;
        this.messagingService = messagingService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserDto userDto, HttpServletRequest request) {
        User user = modelMapper.map(userDto, User.class);
        Map<String, String> tokens = userService.login(user, getClientIp(request));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto userDto) {
        String register = userService.register(modelMapper.map(userDto, User.class));
        return ResponseEntity.ok(register);
    }

    @PostMapping(value = "/token", produces = "application/json")
    public ResponseEntity<Map<String, String>> getTokens(HttpServletRequest request) {
        Map<String, String> tokens = userService.generateNewTokens(request.getHeader("token"), getClientIp(request));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/password/forgot")
    public ResponseEntity<String> sendPasswordResetToken(@RequestBody EmailRequest email) {
        messagingService.send(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/password/reset")
    public ResponseEntity<String> updatePassword(@RequestParam(value = "token") TokenRequest token,
                                                 @RequestBody JsonNode payload) {
        JsonNode password = payload.get("password");

        if (password == null) {
            return ResponseEntity.badRequest().body("Token is required");
        } else {
            passwordFacade.updatePassword(token, password.asText());
            return ResponseEntity.ok().build();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || "".equals(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

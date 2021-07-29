package ru.example.todoapp.controller;
/*
 * Date: 5/15/21
 * Time: 6:10 PM
 * */

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.todoapp.controller.request.CredentialsRequest;
import ru.example.todoapp.dto.UserDto;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.facade.PasswordFacade;
import ru.example.todoapp.messaging.MessagingClient;
import ru.example.todoapp.messaging.request.EmailRequest;
import ru.example.todoapp.messaging.request.TokenRequest;
import ru.example.todoapp.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Api(tags = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordFacade passwordFacade;
    private final MessagingClient messagingService;
    private final ModelMapper modelMapper;

    public AuthController(UserService userService, PasswordFacade passwordFacade,
                          MessagingClient messagingService, ModelMapper modelMapper) {
        this.userService = userService;
        this.passwordFacade = passwordFacade;
        this.messagingService = messagingService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody CredentialsRequest credentials,
                                                     HttpServletRequest request) {
        Map<String, String> tokens = userService.login(credentials, getClientIp(request));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto userDto) {
        userService.register(modelMapper.map(userDto, User.class));
        return ResponseEntity.ok().build();
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

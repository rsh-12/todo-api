package ru.example.todoapp.controller;
/*
 * Date: 5/15/21
 * Time: 6:10 PM
 * */

import io.swagger.annotations.Api;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.example.todoapp.controller.request.CredentialsRequest;
import ru.example.todoapp.controller.request.EmailRequest;
import ru.example.todoapp.controller.request.PasswordRequest;
import ru.example.todoapp.controller.request.TokenRequest;
import ru.example.todoapp.dto.UserDto;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.facade.PasswordFacade;
import ru.example.todoapp.messaging.MessagingClient;
import ru.example.todoapp.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(tags = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordFacade passwordFacade;
    private final MessagingClient messagingService;

    public AuthController(UserService userService, PasswordFacade passwordFacade, MessagingClient messagingService) {
        this.userService = userService;
        this.passwordFacade = passwordFacade;
        this.messagingService = messagingService;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody CredentialsRequest credentials,
                                                     HttpServletRequest request) {
        Map<String, String> tokens = userService.login(credentials, getClientIp(request));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/register", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserDto> register(@Valid @RequestBody CredentialsRequest credentials) {
        User user = userService.register(credentials);
        return EntityModel.of(userService.mapToUserDto(user),
                linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel());
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
                                                 @RequestBody PasswordRequest request) {
        passwordFacade.updatePassword(token, request.getPassword());
        return ResponseEntity.ok().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || "".equals(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

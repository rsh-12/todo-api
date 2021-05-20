package ru.example.todo.controller;
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
import org.springframework.web.bind.annotation.RestController;
import ru.example.todo.domain.request.PasswordRequest;
import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.User;
import ru.example.todo.service.OtpService;
import ru.example.todo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;
    private final ModelMapper modelMapper;

    public AuthController(UserService userService,
                          OtpService otpService, ModelMapper modelMapper) {
        this.userService = userService;
        this.otpService = otpService;
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
    public ResponseEntity<?> createAndSendOtp(@RequestBody JsonNode body) {
        otpService.sendOtp(body);
        return ResponseEntity.ok("Code sent to mail successfully");
    }

    @PostMapping(value = "/password/reset", consumes = "application/json")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordRequest passwordRequest) {
        userService.updatePassword(passwordRequest);
        return ResponseEntity.ok().build();
    }

}

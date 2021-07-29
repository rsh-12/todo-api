package ru.example.todoapp.controller;
/*
 * Date: 3/25/21
 * Time: 4:39 PM
 * */

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.security.UserDetailsImpl;
import ru.example.todoapp.service.UserService;

@Api(tags = "Users")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long userId) {
        return userService.findUserById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping(value = "/password/update", consumes = "application/json")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestBody String password) {
        userService.updatePassword(userDetails.getUsername(), password);
        return ResponseEntity.ok().body("Password updated successfully");
    }

}

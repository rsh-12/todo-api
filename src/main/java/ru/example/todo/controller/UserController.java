package ru.example.todo.controller;
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
import ru.example.todo.entity.User;
import ru.example.todo.security.UserDetailsImpl;
import ru.example.todo.service.UserService;

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
        return userService.getUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping(value = "/password/update", consumes = "application/json")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetailsImpl udi,
                                                 @RequestBody String password) {
        userService.updatePassword(udi.getUser(), password);
        return ResponseEntity.ok().body("Password updated successfully");
    }

}

package ru.example.todoapp.service;
/*
 * Date: 10.07.2021
 * Time: 6:12 PM
 * */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.repository.UserRepository;
import ru.example.todoapp.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    // deleteUserById
    @Test
    public void deleteUserById_ShouldDoNoting() {
        given(userRepository.existsById(anyLong())).willReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUserById(1L);
    }

    @Test
    public void deleteUserById_ShouldThrowException() {
        given(userRepository.existsById(anyLong())).willReturn(false);
        assertThrows(CustomException.class, () -> userService.deleteUserById(1L));
    }

    // findUserById
    @Test
    public void findUserById_ShouldReturnUser() {
        User mockUser = mock(User.class);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(mockUser));

        User user = userService.findUserById(1L).orElse(null);
        assertNotNull(user);
    }

    @Test
    public void findUserById_ShouldReturnEmpty() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        assertEquals(Optional.empty(), userService.findUserById(1L));
    }

    // updatePassword

    // existsByUsername
    @Test
    public void existsByUsername_ShouldReturnTrue() {
        given(userRepository.existsByUsername(anyString())).willReturn(true);
        boolean userExists = userService.existsByUsername("user@mail.com");
        assertTrue(userExists);
    }

    @Test
    public void existsByUsername_ShouldReturnFalse() {
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        boolean userExists = userService.existsByUsername("user@mail.com");
        assertFalse(userExists);
    }

}

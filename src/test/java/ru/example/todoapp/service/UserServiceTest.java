package ru.example.todoapp.service;
/*
 * Date: 10.07.2021
 * Time: 6:12 PM
 * */

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.repository.UserRepository;
import ru.example.todoapp.security.UserDetailsImpl;
import ru.example.todoapp.service.impl.UserServiceImpl;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProperties tokenProperties;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        given(tokenProperties.getAccessTokenValidity()).willReturn(1_800_000L);
        given(tokenProperties.getRefreshTokenValidity()).willReturn(86_400_000L);
    }

    // login
    @Test
    public void login_ShouldAuthUserAndReturnTokens() {
        User user = mock(User.class);
        Authentication auth = mock(Authentication.class);

        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(auth);
        given(auth.getPrincipal()).willReturn(new UserDetailsImpl(user));

        given(jwtTokenService.buildAccessToken(anyLong(), anySet())).willReturn("accessToken");
        given(refreshTokenService.createRefreshToken(anyLong(), anyString())).willReturn("refreshToken");

        Map<String, String> response = userService.login(new User(), "");

        assertEquals("accessToken", response.get("access_token"));
        assertEquals("refreshToken", response.get("refresh_token"));
        assertEquals("Bearer", response.get("token_type"));
        assertEquals("1800000", response.get("access_token_expires"));
        assertEquals("86400000", response.get("refresh_token_expires"));
    }

    @Test
    public void login_ShouldThrowCustomException() {
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(UsernameNotFoundException.class);
        assertThrows(CustomException.class, () -> userService.login(new User(), ""));
    }

    // register
    @Test
    public void register_ShouldThrowException() {
        given(userRepository.existsByUsername(anyString())).willReturn(true);
        assertThrows(CustomException.class, () -> userService.register(new User("user", "pwd")));
    }

    @Test
    public void register_ShouldDoNoting() {
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(new User());
        given(passwordEncoder.encode(anyString())).willReturn("$ecryptedString");
        userService.register(new User("user", "pwd"));
    }

    // generateNewTokens

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

        User user = userService.findUserById(1L);
        assertNotNull(user);
    }

    @Test
    public void findUserById_ShouldThrowCustomException() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        assertThrows(CustomException.class, () -> userService.findUserById(1L));
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

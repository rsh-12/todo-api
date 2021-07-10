package ru.example.todo.service;
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
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.UserRepository;
import ru.example.todo.security.UserDetailsImpl;
import ru.example.todo.service.impl.UserServiceImpl;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
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
    // generateNewTokens
    // deleteUserById
    // findUserById
    // updatePassword
    // existsByUsername

}

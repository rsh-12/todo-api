package ru.example.todoapp.service;
/*
 * Date: 18.08.2021
 * Time: 11:59 AM
 * */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.controller.request.CredentialsRequest;
import ru.example.todoapp.entity.RefreshToken;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.repository.UserRepository;
import ru.example.todoapp.security.UserDetailsImpl;
import ru.example.todoapp.service.impl.AuthServiceImpl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private TokenProperties tokenProperties;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    private static final String USERNAME = "user@mail.com";
    private static final String PASSWORD = "password12345";

    @BeforeEach
    public void setUp() {
        given(tokenProperties.getAccessTokenValidity()).willReturn(1_800_000L);
        given(tokenProperties.getRefreshTokenValidity()).willReturn(86_400_000L);
    }

    // login
    @Test
    @DisplayName("login: returns tokens")
    public void login_ShouldAuthUserAndReturnTokens() {
        User user = mock(User.class);
        Authentication auth = mock(Authentication.class);
        RefreshToken refreshToken = mock(RefreshToken.class);

        // tokens
        given(refreshTokenService.createRefreshToken(anyLong(), anyString())).willReturn(refreshToken);
        given(refreshToken.getToken()).willReturn("refreshToken");
        given(jwtTokenService.buildAccessToken(anyLong(), anySet())).willReturn("accessToken");

        given(auth.getPrincipal()).willReturn(new UserDetailsImpl(user));
        given(authManager.authenticate(any())).willReturn(auth);

        Map<String, String> tokens = authService.login(new CredentialsRequest(USERNAME, PASSWORD), "");

        assertEquals("accessToken", tokens.get("access_token"));
        assertEquals("refreshToken", tokens.get("refresh_token"));
    }

    @Test
    @DisplayName("login: throws an exception")
    public void login_ShouldThrowCustomException() {
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(UsernameNotFoundException.class);

        assertThrows(CustomException.class, () ->
                authService.login(new CredentialsRequest(USERNAME, PASSWORD), ""));
    }

    // register
/*
    @Test
    public void register_ShouldThrowException() {
        given(userRepository.existsByUsername(anyString())).willReturn(true);
        assertThrows(CustomException.class, () -> userService.register(new CredentialsRequest("user", "pwd")));
    }
*/

/*
    @Test
    public void register_ShouldDoNoting() {
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(new User());
        given(passwordEncoder.encode(anyString())).willReturn("$ecryptedString");
        userService.register(new CredentialsRequest("user", "pwd"));
    }
*/

    // generateNewTokens

}

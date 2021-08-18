package ru.example.todoapp.service;
/*
 * Date: 18.08.2021
 * Time: 11:59 AM
 * */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.repository.UserRepository;
import ru.example.todoapp.security.UserDetailsImpl;
import ru.example.todoapp.service.impl.AuthServiceImpl;

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

    @BeforeEach
    public void setUp() {
        given(tokenProperties.getAccessTokenValidity()).willReturn(1_800_000L);
        given(tokenProperties.getRefreshTokenValidity()).willReturn(86_400_000L);
    }

    // login
    @Test
    public void login_ShouldAuthUserAndReturnTokens() {
        User user = mock(User.class);
        Authentication auth = mock(Authentication.class);

        given(authManager.authenticate(any())).willReturn(auth);
        given(auth.getPrincipal()).willReturn(new UserDetailsImpl(user));

        given(jwtTokenService.buildAccessToken(anyLong(), anySet())).willReturn("accessToken");
        given(refreshTokenService.createRefreshToken(anyLong(), anyString())).willReturn(null);

    }

/*
    @Test
    public void login_ShouldThrowCustomException() {
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(UsernameNotFoundException.class);
        assertThrows(CustomException.class, () -> userService.login(new CredentialsRequest("username", "password"), ""));
    }
*/

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

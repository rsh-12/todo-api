package ru.example.todoapp.service;
/*
 * Date: 5/9/21
 * Time: 9:01 AM
 * */

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.enums.Role;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.service.impl.JwtTokenServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
public class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenServiceImpl jwtTokenService;

    @Mock
    private TokenProperties tokenProperties;

    @BeforeEach
    public void setUp() {
        given(tokenProperties.getSecret()).willReturn("secretKey".repeat(5));
        given(tokenProperties.getAccessTokenValidity()).willReturn(1_800_000L);
    }

    // buildAccessToken
    @Test
    public void buildAccessToken_ShouldReturnAccessToken() throws CustomException {
        String accessToken = jwtTokenService.buildAccessToken(1L, Set.of(Role.USER, Role.ADMIN));
        assertNotNull(accessToken);
        assertEquals(3, accessToken.split("\\.").length);
        System.out.println(accessToken);
    }

    @Test
    public void buildRefreshToken_ShouldReturnRefreshToken() throws CustomException {
        String refreshToken = jwtTokenService.buildRefreshToken();
        assertNotNull(refreshToken);
        System.out.println(refreshToken);
    }

    // resolveAccessToken
    @Test
    public void resolveAccessToken_ShouldReturnToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("Bearer someAccessToken");

        String accessToken = jwtTokenService.resolveAccessToken(request);
        assertFalse(accessToken.isEmpty());
        assertEquals("someAccessToken", accessToken);
    }

    @Test
    public void resolveAccessToken_WithLowerCasePrefix_ShouldReturnToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("bearer someAccessToken");

        String accessToken = jwtTokenService.resolveAccessToken(request);
        assertFalse(accessToken.isEmpty());
        assertEquals("someAccessToken", accessToken);
    }

    @Test
    public void resolveAccessToken_WithoutPrefix_ShouldReturnToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("someAccessToken");

        String accessToken = jwtTokenService.resolveAccessToken(request);
        assertFalse(accessToken.isEmpty());
        assertEquals("someAccessToken", accessToken);
    }

    @Test
    public void resolveAccessToken_ShouldReturnEmptyString() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String accessToken = jwtTokenService.resolveAccessToken(request);
        assertEquals("", accessToken);
    }

    // isAccessTokenValid
    @Test
    public void isAccessTokenValid_ShouldReturnTrue() {
        String accessToken = jwtTokenService.buildAccessToken(1L, Collections.singleton(Role.USER));
        boolean isAccessTokenValid = jwtTokenService.isTokenValid(accessToken);
        assertTrue(isAccessTokenValid);
    }

    @Test
    public void isAccessTokenValid_ShouldThrowCustomException() {
        String accessToken = Jwts.builder()
                .setClaims(Map.of("id", 1L))
                .setExpiration(new Date(new Date().getTime() - 1000))
                .signWith(Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(CustomException.class, () -> jwtTokenService.isTokenValid(accessToken));
    }

    // getIdFromAccessToken
    @Test
    public void getId_ShouldReturnUserId() {
        String accessToken = jwtTokenService
                .buildAccessToken(1L, Collections.singleton(Role.USER));
        assertNotNull(accessToken);

        Long id = jwtTokenService.getUserIdFromAccessToken(accessToken);
        assertNotNull(id);

        assertEquals(1L, (long) id);
    }

    // getUserRolesFromAccessToken
    @Test
    public void getUserRoles_ShouldReturnRoles() {
        String accessToken = jwtTokenService
                .buildAccessToken(1L, Set.of(Role.ADMIN, Role.USER));
        assertNotNull(accessToken);

        Set<Role> userRoles = jwtTokenService.getUserRolesFromAccessToken(accessToken);
        assertNotNull(userRoles);

        assertTrue(userRoles.contains(Role.USER));
        assertTrue(userRoles.contains(Role.ADMIN));
    }

    // getAuthentication
    @Test
    public void getAuthentication_ShouldReturnAuthInstance() {
        String accessToken = jwtTokenService.buildAccessToken(1L, Set.of(Role.USER));
        assertNotNull(accessToken);

        Authentication auth = jwtTokenService.getAuthentication(accessToken);
        assertNotNull(auth);
    }

}

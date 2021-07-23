package ru.example.todo.service;
/*
 * Date: 5/9/21
 * Time: 9:01 AM
 * */

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.impl.JwtTokenServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenServiceImpl jwtTokenService;

    @Mock
    private TokenProperties tokenProperties;

    @Before
    public void setUp() {
        given(tokenProperties.getSecret()).willReturn("secretKey".repeat(5));
        given(tokenProperties.getAccessTokenValidity()).willReturn(1_800_000L);
    }

    // buildAccessToken
    @Test
    public void buildAccessToken_ShouldReturnAccessToken() throws CustomException {
        String accessToken = jwtTokenService.buildAccessToken(1L, Set.of(Role.USER));
        assertNotNull(accessToken);
        assertEquals(3, accessToken.split("\\.").length);
        System.out.println("accessToken = " + accessToken);
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
        boolean isAccessTokenValid = jwtTokenService.isAccessTokenValid(accessToken);
        assertTrue(isAccessTokenValid);
    }

    @Test
    public void isAccessTokenValid_ShouldThrowCustomException() {
        String accessToken = Jwts.builder()
                .setClaims(Map.of("id", 1L))
                .setExpiration(new Date(new Date().getTime() - 1000))
                .signWith(Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(CustomException.class, () -> jwtTokenService.isAccessTokenValid(accessToken));
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

package ru.example.todo.service;
/*
 * Date: 5/9/21
 * Time: 9:01 AM
 * */

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

public class JwtTokenServiceTest extends AbstractServiceTestClass {

    @Autowired
    private JwtTokenServiceImpl jwtTokenService;

    @Autowired
    private TokenProperties tokenProperties;

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

    // getId
    @Test
    public void getId_ShouldReturnUserId() {
        String accessToken = jwtTokenService
                .buildAccessToken(1L, Collections.singleton(Role.USER));
        assertNotNull(accessToken);

        Long id = jwtTokenService.getId(accessToken);
        assertNotNull(id);

        assertEquals(1L, (long) id);
    }

    // getUserRoles
    @Test
    public void getUserRoles_ShouldReturnRoles() {
        String accessToken = jwtTokenService
                .buildAccessToken(1L, Set.of(Role.ADMIN, Role.USER));
        assertNotNull(accessToken);

        Set<Role> userRoles = jwtTokenService.getUserRoles(accessToken);
        assertNotNull(userRoles);

        assertTrue(userRoles.contains(Role.USER));
        assertTrue(userRoles.contains(Role.ADMIN));
    }


    // getAuthentication

}

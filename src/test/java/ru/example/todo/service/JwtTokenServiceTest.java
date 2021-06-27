package ru.example.todo.service;
/*
 * Date: 5/9/21
 * Time: 9:01 AM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.impl.JwtTokenServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JwtTokenServiceTest extends AbstractServiceTestClass {

    @Autowired
    private JwtTokenServiceImpl jwtTokenService;

    @MockBean
    private TokenStore tokenStore;

    // buildAccessToken
    @Test
    public void buildAccessToken_ShouldReturnAccessToken() throws CustomException {
        String accessToken = jwtTokenService.buildAccessToken(1L, Set.of(Role.USER));
        assertNotNull(accessToken);
        assertEquals(3, accessToken.split("\\.").length);
        System.out.println("accessToken = " + accessToken);
    }

    // buildRefreshToken
    @Test
    public void buildRefreshToken_ShouldReturnRefreshToken() {
        final String username = "username";
        RefreshToken refreshToken = jwtTokenService.buildRefreshToken(username);

        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getToken());
        assertNotNull(refreshToken.getExpiryTime());
        assertNotNull(refreshToken.getUsername());

        assertEquals(username, refreshToken.getUsername());
        System.out.println("refreshToken = " + refreshToken);
    }

    // resolveAccessToken
    @Test
    public void resolveAccessToken_ShouldReturnToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("Bearer someAccessToken");

        String accessToken = jwtTokenService.resolveAccessToken(request);
        assertNotNull(accessToken);
        assertEquals("someAccessToken", accessToken);
    }


    // isAccessTokenValid
    // findRefreshToken
    // removeRefreshTokenById
    // hasRefreshTokenExpired

    // getUsername
    @Test
    public void getUsername_ShouldReturnUsername() {
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

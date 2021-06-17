package ru.example.todo.service;
/*
 * Date: 5/9/21
 * Time: 9:01 AM
 * */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.security.UserDetailsServiceImpl;
import ru.example.todo.service.impl.JwtTokenServiceImpl;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;

// todo: mock TokenProperties
public class JwtTokenServiceTest extends AbstractServiceTestClass {

    @Autowired
    private JwtTokenServiceImpl jwtTokenService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenStore tokenStore;

    // buildAccessToken
    @Test
    public void buildAccessToken_ShouldReturnAccessToken() throws CustomException {
        String accessToken = jwtTokenService.buildAccessToken("username", Set.of(Role.USER));
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

    // authenticateAndReturnInstance



}

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

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// todo: mock TokenProperties
public class JwtTokenServiceTest extends AbstractServiceTestClass {

    @Autowired
    private JwtTokenServiceImpl jwtTokenService;

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

}

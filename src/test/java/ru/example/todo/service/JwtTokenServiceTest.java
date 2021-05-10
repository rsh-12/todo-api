package ru.example.todo.service;
/*
 * Date: 5/9/21
 * Time: 9:01 AM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.enums.Role;
import ru.example.todo.exception.CustomException;
import ru.example.todo.service.config.JwtTokenServiceImplTestConfig;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

@Import(JwtTokenServiceImplTestConfig.class)
public class JwtTokenServiceTest extends AbstractServiceTestClass {

    @Qualifier("jwtTokenService")
    @Autowired
    JwtTokenService jwtTokenService;

    @Test
    public void validateToken_ShouldThrowException() throws CustomException {
        assertThrows(CustomException.class, () -> jwtTokenService.validateToken("invalid"));
    }

    @Test
    public void buildAccessToken_ShouldReturnAccessToken() throws CustomException {
        String accessToken = getAccessToken("admin", Collections.singleton(Role.ROLE_ADMIN));

        int parts = accessToken.split("\\.").length;
        assertEquals(3, parts);
        System.out.println("accessToken = " + accessToken);
    }

    @Test
    public void isNotExpired_ShouldReturnTrue() {
        RefreshToken refreshToken = getRefreshToken("admin");
        boolean isValid = jwtTokenService.isNotExpired(refreshToken);
        assertTrue(isValid);
    }

    @Test
    public void validateToken_ShouldReturnTrue() {
        String accessToken = getAccessToken("admin", null);
        assertNotNull(accessToken);

        boolean isValidToken = jwtTokenService.validateToken(accessToken);
        assertTrue(isValidToken);
    }

    @Test
    public void findRefreshToken_ShouldThrowCustomException() throws CustomException {
        assertThrows(CustomException.class, () -> jwtTokenService.findRefreshToken(null));
    }

    // Helper methods
    private String getAccessToken(String username, Set<Role> roles) {
        String accessToken = jwtTokenService.buildAccessToken(username, roles);
        assert accessToken != null;
        return accessToken;
    }

    private RefreshToken getRefreshToken(String username) {
        RefreshToken refreshToken = jwtTokenService.buildRefreshToken(username);
        assert refreshToken != null;
        return refreshToken;
    }
}

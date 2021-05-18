package ru.example.todo.service;
/*
 * Date: 5/9/21
 * Time: 9:01 AM
 * */

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public void buildRefreshToken_ShouldReturnRefreshToken() {
        RefreshToken refreshToken = getRefreshToken("admin");

        assertNotNull(refreshToken.getToken());
        assertNotNull(refreshToken.getExpiryTime());
        assertNotNull(refreshToken.getUsername());

        assertEquals("admin", refreshToken.getUsername());
        System.out.println("refreshToken = " + refreshToken);
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

    @Test
    public void findRefreshToken_ShouldReturnToken() throws Exception {
        String refreshToken = getRefreshToken("admin").getToken();
        assertNotNull(refreshToken);

        Thread.sleep(100);
        RefreshToken refreshTokenFromStore = jwtTokenService.findRefreshToken(refreshToken);
        assertNotNull(refreshTokenFromStore);

        String fromStoreToken = refreshTokenFromStore.getToken();
        assertNotNull(fromStoreToken);

        assertEquals(refreshToken, fromStoreToken);
    }

    @Test
    public void getAuthentication_ShouldReturnUsernamePasswordAuthToken() throws Exception {
        String accessToken = getAccessToken("admin@mail.com", Collections.singleton(Role.ROLE_ADMIN));
        Authentication authentication = jwtTokenService.getAuthentication(accessToken);
        assertNotNull(authentication);

        boolean isAuthenticated = authentication.isAuthenticated();
        assertTrue(isAuthenticated);

        String username = authentication.getName();
        assertEquals("admin@mail.com", username);
    }

    @Test
    public void getAuthentication_ShouldThrowUsernameNotFoundException() throws UsernameNotFoundException {
        String accessToken = getAccessToken("admin", Collections.singleton(Role.ROLE_ADMIN));
        assertThrows(UsernameNotFoundException.class, () -> jwtTokenService.getAuthentication(accessToken));
    }

    @Test
    public void removeOldRefreshToken_ShouldRemoveTokenFromTokenStore() throws InterruptedException {

        // create refresh token and save it
        String refreshToken = getRefreshToken("admin").getToken();
        assertNotNull(refreshToken);

        Thread.sleep(100);
        RefreshToken refreshTokenFromStore = jwtTokenService.findRefreshToken(refreshToken);
        assertNotNull(refreshTokenFromStore);

        // delete refresh token from tokenStore
        jwtTokenService.removeOldRefreshTokenById(refreshToken);

        RefreshToken deletedRefreshTokenFromStore = jwtTokenService.findRefreshToken(refreshToken);
        assertNull(deletedRefreshTokenFromStore);
    }

    @Test
    public void resolveToken_ShouldReturnNull() {
        var request = new MockHttpServletRequest();
        String result = jwtTokenService.resolveToken(request);
        assertNull(result);
    }

    @Test
    public void resolveToken_ShouldReturnToken() {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "bearer some-token");

        String result = jwtTokenService.resolveToken(request);
        assertEquals("some-token", result);
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
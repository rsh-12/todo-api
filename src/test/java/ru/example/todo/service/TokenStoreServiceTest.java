package ru.example.todo.service;
/*
 * Date: 5/11/21
 * Time: 9:30 PM
 * */

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.todo.domain.RefreshToken;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;

public class TokenStoreServiceTest extends AbstractServiceTestClass {

    @Autowired
    private TokenStore tokenStore;

    private final static String TOKEN = "some-token";

    @Before
    public void setUp() throws Exception {
        Instant instant = Instant.now().minus(Duration.ofDays(10));
        Date expiryTime = Date.from(instant);

        RefreshToken refreshToken = new RefreshToken(
                TOKEN,
                "user12@mail.com",
                expiryTime);

        tokenStore.saveRefreshToken(refreshToken);
        Thread.sleep(100);
    }


    @Test
    public void findRefreshToken_ShouldReturnToken() {
        RefreshToken refreshToken = tokenStore.findRefreshToken(TOKEN).orElse(null);
        assertNotNull(refreshToken);
        assertEquals(TOKEN, refreshToken.getToken());
    }

    @Test
    public void findRefreshToken_ShouldReturnNull() {
        RefreshToken refreshToken = tokenStore.findRefreshToken("notfound").orElse(null);
        assertNull(refreshToken);
    }

    @Test
    public void removeIfExpired_ShouldRemoveTokenFromStore() {
        RefreshToken before = tokenStore.findRefreshToken(TOKEN).orElse(null);
        assertNotNull(before);

        tokenStore.deleteExpiredRefreshTokens();

        RefreshToken after = tokenStore.findRefreshToken(TOKEN).orElse(null);
        assertNull(after);
    }
}

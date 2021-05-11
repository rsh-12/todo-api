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

        tokenStore.save(refreshToken);
        Thread.sleep(100);
    }


    @Test
    public void findRefreshToken_ShouldReturnToken() throws Exception {
        RefreshToken refreshToken = tokenStore.find(TOKEN);
        assertNotNull(refreshToken);
        assertEquals(TOKEN, refreshToken.getToken());
    }

    @Test
    public void findRefreshToken_ShouldReturnNull() {
        RefreshToken refreshToken = tokenStore.find("notfound");
        assertNull(refreshToken);
    }

    @Test
    public void removeIfExpired_ShouldRemoveTokenFromStore() {
        RefreshToken before = tokenStore.find(TOKEN);
        assertNotNull(before);

        tokenStore.removeIfExpired();

        RefreshToken after = tokenStore.find(TOKEN);
        assertNull(after);
    }
}

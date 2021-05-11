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

public class InMemoryTokenStoreServiceTest extends AbstractServiceTestClass {

    @Autowired
    private TokenStore tokenStore;

    @Before
    public void setUp() throws Exception {
        Instant instant = Instant.now().minus(Duration.ofDays(10));
        Date expiryTime = Date.from(instant);

        RefreshToken refreshToken = new RefreshToken(
                "some-token",
                "user12@mail.com",
                expiryTime);

        tokenStore.save(refreshToken);
        Thread.sleep(100);
    }


    @Test
    public void findRefreshToken_ShouldReturnToken() throws Exception {
        RefreshToken refreshToken = tokenStore.find("some-token");
        assertNotNull(refreshToken);
        assertEquals("some-token", refreshToken.getToken());
    }

    @Test
    public void findRefreshToken_ShouldReturnNull() {
        RefreshToken refreshToken = tokenStore.find("notfound");
        assertNull(refreshToken);
    }
}

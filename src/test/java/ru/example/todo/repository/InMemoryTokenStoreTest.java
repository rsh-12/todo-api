package ru.example.todo.repository;
/*
 * Date: 3/26/21
 * Time: 6:08 PM
 * */

import org.junit.jupiter.api.Test;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.service.impl.InMemoryTokenStore;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTokenStoreTest {

    private final InMemoryTokenStore inMemoryTokenStore = new InMemoryTokenStore();

    @Test
    public void saveRefreshToken_ShouldSaveTokensDistinct() {
        Map<String, RefreshToken> tokenStore = initTokenStore();
        assertFalse(tokenStore.isEmpty());
        assertEquals(2, tokenStore.size());
    }

    @Test
    public void findRefreshToken_ShouldReturnFalse() {
        Optional<RefreshToken> refreshToken = inMemoryTokenStore.findRefreshToken("token");
        assertFalse(refreshToken.isPresent());
    }

    @Test
    public void findRefreshToken_ShouldReturnToken() {
        initTokenStore();
        Optional<RefreshToken> refreshToken = inMemoryTokenStore.findRefreshToken("id2");
        assertTrue(refreshToken.isPresent());
        assertEquals("id2", refreshToken.get().getToken());
    }

    @Test
    public void deleteRefreshTokenById_ShouldDeleteToken() {
        Map<String, RefreshToken> tokenStore = initTokenStore();
        assertEquals(2, tokenStore.size());

        inMemoryTokenStore.deleteRefreshTokenById("id4");
        assertEquals(1, tokenStore.size());
    }

    public RefreshToken buildRefreshToken(String token, String username) {
        return new RefreshToken(token, username, new Date(System.currentTimeMillis() + 30000));
    }

    private Map<String, RefreshToken> initTokenStore() {
        Map<String, RefreshToken> tokenStore = inMemoryTokenStore.getTokenStore();

        RefreshToken rt1 = buildRefreshToken("id1", "john");
        RefreshToken rt2 = buildRefreshToken("id2", "john");
        RefreshToken rt3 = buildRefreshToken("id3", "lika");
        RefreshToken rt4 = buildRefreshToken("id4", "lika");

        inMemoryTokenStore.saveRefreshToken(rt1);
        inMemoryTokenStore.saveRefreshToken(rt2);
        inMemoryTokenStore.saveRefreshToken(rt3);
        inMemoryTokenStore.saveRefreshToken(rt4);
        return tokenStore;
    }

}

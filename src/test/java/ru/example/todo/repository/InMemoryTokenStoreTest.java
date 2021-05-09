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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class InMemoryTokenStoreTest {

    private final InMemoryTokenStore inMemoryTokenStore = new InMemoryTokenStore();

    @Test
    public void testTokenStore_ClearIfNameAlreadyExists() {

        Map<String, RefreshToken> tokenStore = initTokenStore();

        assertFalse(tokenStore.isEmpty());
        assertEquals(2, tokenStore.size());
    }

    private Map<String, RefreshToken> initTokenStore() {
        Map<String, RefreshToken> tokenStore = inMemoryTokenStore.getTokenStore();

        RefreshToken rt1 = buildRefreshToken("id1", "john");
        RefreshToken rt2 = buildRefreshToken("id2", "john");
        RefreshToken rt3 = buildRefreshToken("id3", "lika");
        RefreshToken rt4 = buildRefreshToken("id4", "lika");

        inMemoryTokenStore.save(rt1);
        inMemoryTokenStore.save(rt2);
        inMemoryTokenStore.save(rt3);
        inMemoryTokenStore.save(rt4);
        return tokenStore;
    }

    public RefreshToken buildRefreshToken(String token, String username) {
        return new RefreshToken(token, username, new Date(System.currentTimeMillis() + 30000));
    }

}

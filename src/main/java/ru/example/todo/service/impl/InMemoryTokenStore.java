package ru.example.todo.service.impl;
/*
 * Date: 3/25/21
 * Time: 1:15 PM
 * */

import org.springframework.scheduling.annotation.Async;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.service.TokenStore;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTokenStore implements TokenStore {

    private final Map<String, RefreshToken> tokenStore = new ConcurrentHashMap<>();

    @Override
    public RefreshToken findRefreshToken(String token) {
        return tokenStore.get(token);
    }

    @Override
    @Async
    public CompletableFuture<Void> saveRefreshToken(RefreshToken refreshToken) {

        tokenStore.values().stream()
                .filter(token -> token.getUsername().equals(refreshToken.getUsername()))
                .forEach(token -> tokenStore.remove(token.getToken()));

        tokenStore.put(refreshToken.getToken(), refreshToken);

        return CompletableFuture.allOf();
    }

    @Override
    public void deleteRefreshTokenById(String tokenId) {
        tokenStore.remove(tokenId);
    }

    @Override
    public void deleteExpiredRefreshTokens() {
        tokenStore.values().stream()
                .filter(token -> token.getExpiryTime().before(new Date(System.currentTimeMillis())))
                .forEach(token -> tokenStore.remove(token.getToken()));
    }

    public Map<String, RefreshToken> getTokenStore() {
        return tokenStore;
    }
}

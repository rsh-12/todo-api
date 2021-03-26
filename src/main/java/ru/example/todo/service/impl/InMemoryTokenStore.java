package ru.example.todo.service.impl;
/*
 * Date: 3/25/21
 * Time: 1:15 PM
 * */

import org.springframework.scheduling.annotation.Async;
import ru.example.todo.entity.RefreshToken;
import ru.example.todo.service.TokenStore;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTokenStore implements TokenStore {

    private final Map<String, RefreshToken> tokenStore = new ConcurrentHashMap<>();

    @Override
    public RefreshToken findById(String tokenId) {
        return tokenStore.get(tokenId);
    }

    @Override
    @Async
    public CompletableFuture<Void> save(RefreshToken refreshToken) {

        tokenStore.values().stream()
                .filter(token -> token.getUsername().equals(refreshToken.getUsername()))
                .forEach(token -> tokenStore.remove(token.getId()));

        tokenStore.put(refreshToken.getId(), refreshToken);

        return CompletableFuture.allOf();
    }

    @Override
    public void deleteById(String tokenId) {
        tokenStore.remove(tokenId);
    }

    @Override
    public void removeIfExpired() {
        tokenStore.values().stream()
                .filter(token -> token.getExpiryTime().before(new Date(System.currentTimeMillis())))
                .forEach(token -> tokenStore.remove(token.getId()));
    }

    public Map<String, RefreshToken> getTokenStore() {
        return tokenStore;
    }
}

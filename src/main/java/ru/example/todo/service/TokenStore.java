package ru.example.todo.service;

import ru.example.todo.domain.RefreshToken;

import java.util.concurrent.CompletableFuture;

public interface TokenStore {

    RefreshToken find(String token);

    CompletableFuture<Void> save(RefreshToken refreshToken);

    void deleteById(String tokenId);

    void removeIfExpired();
}

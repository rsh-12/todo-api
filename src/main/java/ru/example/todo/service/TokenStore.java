package ru.example.todo.service;

import ru.example.todo.entity.RefreshToken;

import java.util.concurrent.CompletableFuture;

public interface TokenStore {

    RefreshToken findById(String tokenId);

    CompletableFuture<Void> save(RefreshToken refreshToken);

    void deleteById(String tokenId);

    void removeIfExpired();
}

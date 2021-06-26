package ru.example.todo.service;

import ru.example.todo.domain.RefreshToken;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TokenStore {

    Optional<RefreshToken> findRefreshToken(String token);

    CompletableFuture<Void> saveRefreshToken(RefreshToken refreshToken);

    void deleteRefreshTokenById(String tokenId);

    void deleteExpiredRefreshTokens();
}

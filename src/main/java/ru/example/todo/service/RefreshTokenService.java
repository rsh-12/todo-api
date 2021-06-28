package ru.example.todo.service;

import ru.example.todo.entity.RefreshToken;

public interface RefreshTokenService {

    String createRefreshToken(Long userId, String ip);

    RefreshToken save(RefreshToken refreshToken);

    RefreshToken findRefreshTokenByValue(String refreshToken);

    RefreshToken findRefreshTokenByUserId(Long userId);

    boolean hasRefreshTokenExpired(RefreshToken oldRefreshToken);

}

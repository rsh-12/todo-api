package ru.example.todo.service;

import ru.example.todo.entity.RefreshToken;

public interface TokenStore {

    RefreshToken findById(String tokenId);

    RefreshToken save(RefreshToken refreshToken);

    void deleteById(String tokenId);

    void removeIfExpired();
}

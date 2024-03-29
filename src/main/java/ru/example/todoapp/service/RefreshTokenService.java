package ru.example.todoapp.service;

import ru.example.todoapp.entity.RefreshToken;

import java.util.Optional;

/**
 * This interface consists methods for working with the Refresh token.
 * <p>
 * {@link RefreshToken}
 */
public interface RefreshTokenService {

    /**
     * Creates an instance of the RefreshToken class
     * with the user id, expiration date and a random unique value.
     *
     * @param userId the user id
     * @param ip     the ip address
     * @return the refresh token value as a string
     * @see RefreshToken
     */
    RefreshToken create(Long userId, String ip);

    /**
     * Saves the refresh token to the database.
     *
     * @param refreshToken the refresh token
     * @return the refresh token
     * @see RefreshToken
     */
    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findOne(String refreshToken);

}

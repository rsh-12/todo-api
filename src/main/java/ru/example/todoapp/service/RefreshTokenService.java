package ru.example.todoapp.service;

import ru.example.todoapp.entity.RefreshToken;

/**
 * This interface consists methods for working with the Refresh token.
 *
 * @see RefreshToken
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
    String createRefreshToken(Long userId, String ip);

    /**
     * Saves the refresh token to the database.
     *
     * @param refreshToken the refresh token
     * @return the refresh token
     * @see RefreshToken
     */
    RefreshToken saveRefreshToken(RefreshToken refreshToken);

    /**
     * Finds a refresh token by value in the database.
     * This method call the validateToken method,
     * which check the expiration date and the fact
     * that the refresh token itself exists.
     *
     * @param refreshToken the refresh token
     * @return the refresh token
     * @see RefreshToken
     */
    RefreshToken findRefreshTokenByValue(String refreshToken);

    /**
     * Finds a refresh token by user id in the database.
     * This method call the validateToken method,
     * which check the expiration date and the fact
     * that the refresh token itself exists.
     *
     * @param userId the user id
     * @return the refresh token
     * @see RefreshToken
     */
    RefreshToken findRefreshTokenByUserId(Long userId);

    /**
     * This method checks whether the refresh token has expired or not.
     *
     * @param oldRefreshToken the old refresh token
     * @return the <b>true</b> if the refresh token has expired, otherwise <b>false</b>
     * @see RefreshToken
     */
    boolean hasRefreshTokenExpired(RefreshToken oldRefreshToken);

}

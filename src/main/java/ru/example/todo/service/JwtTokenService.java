package ru.example.todo.service;

import org.springframework.security.core.Authentication;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.enums.Role;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * The interface contains methods for working with the JWT.
 */
public interface JwtTokenService {

    /**
     * Builds an access JWT.
     *
     * @param username the username for building access token.
     * @param roles    the roles for building access token.
     * @return the access JWT
     */
    String buildAccessToken(Long userId, String username, Set<Role> roles);

    /**
     * Builds a refresh JWT.
     *
     * @param username the username for building refresh token.
     * @return the refresh JWT.
     */
    RefreshToken buildRefreshToken(String username);

    /**
     * Gets the access token from header param "Authorization".
     *
     * @param request the http request
     * @return the access token from request.
     */
    String resolveAccessToken(HttpServletRequest request);

    /**
     * Validates the access token.
     *
     * @param accessToken the access token.
     * @return the boolean: <b>true</b> if the token is valid, <b>false</b> otherwise.
     */
    boolean isAccessTokenValid(String accessToken);

    /**
     * Looks for a refresh token in the store.
     *
     * @param refreshToken the refresh token.
     * @return the refresh token.
     */
    RefreshToken findRefreshToken(String refreshToken);

    /**
     * Removes the refresh token by id.
     *
     * @param tokenId the refresh token id.
     */
    void removeRefreshTokenById(String tokenId);

    /**
     * Checks if the refresh token has expired.
     *
     * @param refreshToken the refresh token.
     * @return the boolean: <и>true</и> if the token has not expired, <b>false</b> otherwise.
     */
    boolean hasRefreshTokenExpired(RefreshToken refreshToken);

    String getUsername(String accessToken);

    Set<Role> getUserRoles(String accessToken);

    Authentication getAuthentication(String accessToken);
}

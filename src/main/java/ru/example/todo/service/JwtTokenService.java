package ru.example.todo.service;

import org.springframework.security.core.Authentication;
import ru.example.todo.entity.RefreshToken;
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
     * @param roles the roles for building access token.
     * @return the access JWT
     */
    String buildAccessToken(Long userId, Set<Role> roles);

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

    Long getId(String accessToken);

    Set<Role> getUserRoles(String accessToken);

    Authentication getAuthentication(String accessToken);
}

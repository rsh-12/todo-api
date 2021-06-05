package ru.example.todo.service;

import org.springframework.security.core.Authentication;
import ru.example.todo.domain.RefreshToken;
import ru.example.todo.enums.Role;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

public interface JwtTokenService {

    String buildAccessToken(String username, Set<Role> roles);

    RefreshToken buildRefreshToken(String username);

    Authentication authenticateAndReturnInstance(String token);

    String resolveAccessToken(HttpServletRequest request);

    boolean isAccessTokenValid(String token);

    RefreshToken findRefreshToken(String token);

    void removeRefreshTokenById(String tokenId);

    boolean hasRefreshTokenExpired(RefreshToken refreshToken);
}

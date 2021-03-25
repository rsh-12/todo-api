package ru.example.todo.service;

import org.springframework.security.core.Authentication;
import ru.example.todo.entity.RefreshToken;
import ru.example.todo.entity.Role;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

public interface JwtTokenService {

    String buildAccessToken(String username, Set<Role> roles);

    RefreshToken buildRefreshToken(String username);

    Authentication getAuthentication(String token);

    String resolveToken(HttpServletRequest request);

    boolean validateToken(String token);

    RefreshToken findRefreshTokenById(String tokenId);

    void removeOldRefreshTokenById(String tokenId);

    boolean isValidRefreshToken(RefreshToken refreshToken);
}

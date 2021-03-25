package ru.example.todo.service.impl;
/*
 * Date: 3/25/21
 * Time: 1:05 PM
 * */

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.example.todo.entity.RefreshToken;
import ru.example.todo.entity.Role;
import ru.example.todo.service.JwtTokenService;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    @Override
    public String buildAccessToken(String username, Set<Role> roles) {
        return null;
    }

    @Override
    public String buildAccessToken(String username) {
        return null;
    }

    @Override
    public Authentication getAuthentication(String token) {
        return null;
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public RefreshToken findRefreshTokenById(String tokenId) {
        return null;
    }

    @Override
    public void removeOldRefreshTokenById(String tokenId) {

    }

    @Override
    public boolean isValidRefreshToken(RefreshToken refreshToken) {
        return false;
    }
}

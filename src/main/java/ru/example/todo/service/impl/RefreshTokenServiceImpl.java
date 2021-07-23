package ru.example.todo.service.impl;
/*
 * Date: 6/29/21
 * Time: 12:54 AM
 * */

import org.springframework.stereotype.Service;
import ru.example.todo.config.properties.TokenProperties;
import ru.example.todo.entity.RefreshToken;
import ru.example.todo.exception.CustomException;
import ru.example.todo.repository.RefreshTokenRepository;
import ru.example.todo.service.RefreshTokenService;
import ru.example.todo.util.RandomString;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final TokenProperties tokenProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(TokenProperties tokenProperties, RefreshTokenRepository refreshTokenRepository) {
        this.tokenProperties = tokenProperties;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public String createRefreshToken(Long userId, String ip) {
        RefreshToken refreshToken;
        String token = new RandomString(64).nextString();

        long now = new Date().getTime();
        Date expiresAt = new Date(now + tokenProperties.getRefreshTokenValidity());

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(userId);
        if (optionalRefreshToken.isPresent()) {
            refreshToken = optionalRefreshToken.get();
            refreshToken.setValue(token);
            refreshToken.setExpiresAt(expiresAt);
        } else {
            refreshToken = new RefreshToken(token, userId, expiresAt);
        }

        if (ip != null) refreshToken.setCreatedByIp(ip);
        saveRefreshToken(refreshToken);

        return token;
    }

    @Override
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken findRefreshTokenByValue(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByValue(refreshToken);
        return validateToken(token);
    }

    @Override
    public RefreshToken findRefreshTokenByUserId(Long userId) {
        Optional<RefreshToken> token = refreshTokenRepository.findByUserId(userId);
        return validateToken(token);
    }

    @Override
    public boolean hasRefreshTokenExpired(RefreshToken oldRefreshToken) {
        return Instant.now().isAfter(oldRefreshToken.getExpiresAt().toInstant());
    }

    private RefreshToken validateToken(Optional<RefreshToken> token) {
        if (token.isPresent() && !hasRefreshTokenExpired(token.get())) {
            return token.get();
        }
        throw CustomException.notFound("Refresh token not found/expired");
    }


}

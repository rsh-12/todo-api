package ru.example.todoapp.service.impl;
/*
 * Date: 6/29/21
 * Time: 12:54 AM
 * */

import org.springframework.stereotype.Service;
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.entity.RefreshToken;
import ru.example.todoapp.exception.CustomException;
import ru.example.todoapp.repository.RefreshTokenRepository;
import ru.example.todoapp.service.RefreshTokenService;
import ru.example.todoapp.util.RandomStringGenerator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        String token = new RandomStringGenerator(64).nextString();
        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(tokenProperties.getRefreshTokenValidity(), ChronoUnit.MILLIS);

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken(userId));
        refreshToken.setValue(token);
        refreshToken.setExpiresAt(expiresAt);

        Optional.ofNullable(ip).ifPresent(refreshToken::setCreatedByIp);
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
        return LocalDateTime.now().isAfter(oldRefreshToken.getExpiresAt());
    }

    private RefreshToken validateToken(Optional<RefreshToken> token) {
        if (token.isPresent() && !hasRefreshTokenExpired(token.get())) {
            return token.get();
        }
        throw CustomException.notFound("Refresh token not found/expired");
    }


}

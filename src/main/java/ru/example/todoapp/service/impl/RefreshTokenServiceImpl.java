package ru.example.todoapp.service.impl;
/*
 * Date: 6/29/21
 * Time: 12:54 AM
 * */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.example.todoapp.config.properties.TokenProperties;
import ru.example.todoapp.entity.RefreshToken;
import ru.example.todoapp.repository.RefreshTokenRepository;
import ru.example.todoapp.service.JwtTokenService;
import ru.example.todoapp.service.RefreshTokenService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final TokenProperties tokenProperties;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtTokenService jwtTokenService,
                                   TokenProperties tokenProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenService = jwtTokenService;
        this.tokenProperties = tokenProperties;
    }

    @Override
    public RefreshToken createRefreshToken(Long userId, String ip) {
        String token = jwtTokenService.buildRefreshToken();

        var refreshToken = new RefreshToken(token, userId, ip);
        long refreshValidity = tokenProperties.getRefreshTokenValidity();
        refreshToken.setExpiresAt(LocalDateTime.now().plus(refreshValidity, ChronoUnit.MILLIS));

        return saveRefreshToken(refreshToken);
    }

    @Override
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findRefreshTokenByValue(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(refreshToken -> jwtTokenService.isTokenValid(refreshToken.getToken()));
    }


}

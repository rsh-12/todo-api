package ru.example.todoapp.service.impl;
/*
 * Date: 6/29/21
 * Time: 12:54 AM
 * */

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.example.todoapp.entity.RefreshToken;
import ru.example.todoapp.repository.RefreshTokenRepository;
import ru.example.todoapp.service.JwtTokenService;
import ru.example.todoapp.service.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
            JwtTokenService jwtTokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public RefreshToken create(Long userId, String ip) {
        String token = jwtTokenService.buildRefreshToken();

        RefreshToken refresh = refreshTokenRepository
                .findByUserId(userId)
                .orElseGet(RefreshToken::new);

        refresh.setToken(token);
        refresh.setUserId(userId);
        refresh.setCreatedByIp(ip);

        Instant expTime = jwtTokenService.getExpiration(token).toInstant();
        refresh.setExpiresAt(LocalDateTime.ofInstant(expTime, ZoneId.systemDefault()));

        CompletableFuture.runAsync(() -> refreshTokenRepository.save(refresh));

        return refresh;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findOne(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(refreshToken -> jwtTokenService.isTokenValid(refreshToken.getToken()));
    }

}
